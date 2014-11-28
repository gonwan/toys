#ifdef _WIN32
#include "threadpool.h"
#include "list.h"
#include <windows.h>
#include <stdio.h>


typedef struct _job_t {
    thread_func_t threadfunc;
    void *arg;
} job_t;

typedef enum _worker_state_e {
    WK_IDLE,
    WK_RUNNING,
    WK_TERMINATED
} worker_state_e;

typedef struct _worker_t {
    HANDLE handle;
    volatile worker_state_e state;
    LPTHREAD_START_ROUTINE func;
    void *arg;
} worker_t;

typedef enum _thread_pool_state_e {
    TP_NONE,
    TP_JOB_ADDED,
    TP_TERMINATING,
    TP_TERMINATED
} thread_pool_state_e;

struct _thread_pool_t {
    /* max thread count to create */
    int max;
    /* state set when a new job is added or the thread pool is terminated */
    volatile thread_pool_state_e state;
    /* created worker list */
    list_t *woker_list;
    /* current waiting job list */
    list_t *job_list;
    /* mutex to lock the job list */
    HANDLE job_list_mutex;
    /* event to set when there is any new job added into the job list */
    HANDLE job_added_event;
    /* event to set when terminated */
    HANDLE terminated_event;
};


static DWORD WINAPI thread_pool_internal_callback(void *arg)
{
    worker_t *worker;
    thread_pool_t *pool;
    list_t *jobnode;
    job_t *job;
    HANDLE handles[2];
    DWORD ret;

    worker = (worker_t *)arg;
    pool = (thread_pool_t *)worker->arg;
    handles[0] = pool->job_added_event;
    handles[1] = pool->terminated_event;
    while (1) {
        /* if job list is not empty, get one */
        job = NULL;
        while (1) {
            WaitForSingleObject(pool->job_list_mutex, INFINITE);
            if (list_length(pool->job_list) == 0) {
                ReleaseMutex(pool->job_list_mutex);
                ret = WaitForMultipleObjects(2, handles, FALSE, INFINITE);
                if (ret = WAIT_OBJECT_0 + 1) {
                    worker->state = WK_TERMINATED;
                    break;
                }
            } else {
                jobnode = pool->job_list;
                job = (job_t *)jobnode->data;
                pool->job_list = list_remove_link(jobnode, jobnode);
                ReleaseMutex(pool->job_list_mutex);
                break;
            }
        }
        /* do not check status, since we are not protected by mutex now */
        if (!job) {
            break; /* terminated */
        } else {
            worker->state = WK_RUNNING;
            job->threadfunc(job->arg);
            worker->state = WK_IDLE;
            free(job);
            list_free(jobnode);
        }
    }

    return 0;
}

thread_pool_t *thread_pool_create(int max)
{
    int i, rc;
    worker_t *worker;

    thread_pool_t *pool = (thread_pool_t *)malloc(sizeof(thread_pool_t));
    pool->max = max;
    pool->state = TP_NONE;
    pool->woker_list = NULL;
    pool->job_list = NULL;
    pool->job_list_mutex = CreateMutexA(NULL, FALSE, NULL);
    pool->job_added_event = CreateEventA(NULL, FALSE, FALSE, NULL);
    pool->terminated_event = CreateEventA(NULL, TRUE, FALSE, NULL);
    rc = 0;
    for (i = 0; i < pool->max; i++) {
        worker = (worker_t *)malloc(sizeof(worker_t));
        worker->state = WK_IDLE;
        worker->func = thread_pool_internal_callback;
        worker->arg = pool;
        worker->handle = CreateThread(NULL, 0, worker->func, worker, 0, NULL);
        if (worker->handle == NULL) {
            rc = -1;
            break;
        }
        pool->woker_list = list_append(pool->woker_list, worker);
    }
    if (rc) {
        thread_pool_terminate(pool, 0, 5);
        pool = NULL;
    }
    return pool;
}

void thread_pool_terminate(thread_pool_t *pool, int wait, int timeout)
{
    list_t* temp;
    job_t* job;
    worker_t *worker;
    int tick, finished;

    pool->state = TP_TERMINATING;
    if (!wait) { /* clear job list */
        WaitForSingleObject(pool->job_list_mutex, INFINITE);
        temp = pool->job_list;
        while (temp) {
            job = (job_t *)temp->data;
            temp = temp->next;
            free(job);
        }
        list_free_all(pool->job_list);
        pool->job_list = NULL;
        ReleaseMutex(pool->job_list_mutex);
    } else { /* wait job list */
        while (1) {
            WaitForSingleObject(pool->job_list_mutex, INFINITE);
            if (list_length(pool->job_list) == 0) {
                ReleaseMutex(pool->job_list_mutex);
                break;
            } else {
                ReleaseMutex(pool->job_list_mutex);
                Sleep(1000);
            }
        }
    }
    /* now the job list is empty */
    pool->state = TP_TERMINATED;
    SetEvent(pool->terminated_event);
    /* wait for idle threads */
    tick = timeout;
    while (tick > 0) {
        static const int t = 5;
        Sleep(t*1000);
        tick -= t;
        finished = 1;
        temp = pool->woker_list;
        while (temp) {
            worker = (worker_t *)temp->data;
            temp = temp->next;
            if (worker->state != WK_TERMINATED) {
                finished = 0;
            } else {
                if (worker->handle != NULL) {
                    finished = 0;
                    WaitForSingleObject(worker->handle, INFINITE);
                    CloseHandle(worker->handle);
                    worker->handle = NULL;
                }
            }
        }
        if (finished) {
            break;
        }
    }
    /* kill hanging threads */
    temp = pool->woker_list;
    while (temp) {
        worker = (worker_t *)temp->data;
        temp = temp->next;
        if (worker->state != WK_TERMINATED) {
            if (worker->handle != NULL) {
#if 0
                /* GetThreadId() requires windows 2003 or later */
                fprintf(stderr, "terminating thread %p\n", worker->handle);
#endif
                /*
                 * Suppose to be safe here.
                 * In implementation of apr and glib, they just abandon the hanging threads.
                 */
                TerminateThread(worker->handle, -1);
                CloseHandle(worker->handle);
                worker->handle = NULL;
            }
        }
        free(worker);
    }
    list_free_all(pool->woker_list);
    pool->woker_list = NULL;
    /* free thread pool */
    CloseHandle(pool->job_list_mutex);
    CloseHandle(pool->job_added_event);
    CloseHandle(pool->terminated_event);
    free(pool);
}

int thread_pool_add_job(thread_pool_t *pool, thread_func_t func, void *arg)
{
    job_t *job;

    if (pool->state == TP_TERMINATING || pool->state == TP_TERMINATED) {
        return -1;
    }
    job = (job_t *)malloc(sizeof(job_t));
    job->threadfunc = func;
    job->arg = arg;
    WaitForSingleObject(pool->job_list_mutex, INFINITE);
    pool->job_list = list_append(pool->job_list, job);
    ReleaseMutex(pool->job_list_mutex);
    pool->state = TP_JOB_ADDED;
    SetEvent(pool->job_added_event); /* not necessary between mutex lock */
    return 0;
}


#endif
