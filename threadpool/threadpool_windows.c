#ifdef _WIN32
#include "threadpool.h"
#include "list.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <windows.h>


typedef struct _job_t {
    list_t link;
    thread_func_t threadfunc;
    void *arg;
} job_t;

typedef enum _worker_state_e {
    WK_IDLE,
    WK_RUNNING,
    WK_TERMINATED
} worker_state_e;

typedef struct _worker_t {
    list_t link;
    HANDLE handle;
    volatile worker_state_e state;
    LPTHREAD_START_ROUTINE func;
    void *arg;
} worker_t;

typedef enum _thread_pool_state_e {
    TP_RUNNING,
    TP_TERMINATING,
    TP_TERMINATED
} thread_pool_state_e;

struct _thread_pool_t {
    /* number of threads to create */
    size_t size;
    /* state set when a new job is added or the thread pool is terminated */
    volatile thread_pool_state_e state;
    /* created worker list */
    list_t worker_list;
    /* current waiting job list */
    list_t job_list;
    /* mutex to lock the job list */
    HANDLE job_list_mutex;
    /* event to set when there is any new job added into the job list */
    HANDLE job_added_event;
};


static DWORD WINAPI thread_pool_internal_callback(void *arg)
{
    thread_pool_t *pool;
    job_t *job;
    worker_t *worker;

    worker = (worker_t *)arg;
    pool = (thread_pool_t *)worker->arg;

    while (1) {
        if (pool->state == TP_TERMINATED) {
            worker->state = WK_TERMINATED;
            break;
        }
        /* if job list is not empty, get one */
        job = NULL;
        WaitForSingleObject(pool->job_added_event, INFINITE);
        WaitForSingleObject(pool->job_list_mutex, INFINITE);
        if (!list_empty(&pool->job_list)) {
            job = (job_t *)pool->job_list.next;
            list_del((list_t *)job);
        }
        ReleaseMutex(pool->job_list_mutex);
        /* do not check status, since we are not protected by mutex now */
        if (job) {
            worker->state = WK_RUNNING;
            job->threadfunc(job->arg);
            worker->state = WK_IDLE;
            free(job);
        }
    }

    return 0;
}

thread_pool_t *thread_pool_create(size_t size)
{
    int rc;
    size_t i;
    worker_t *worker;

    thread_pool_t *pool = (thread_pool_t *)malloc(sizeof(thread_pool_t));
    memset(pool, 0, sizeof(thread_pool_t));
    pool->size = size;
    pool->state = TP_RUNNING;
    LIST_HEAD_INIT(&pool->worker_list);
    LIST_HEAD_INIT(&pool->job_list);
    pool->job_list_mutex = CreateMutexA(NULL, FALSE, NULL);
    pool->job_added_event = CreateEventA(NULL, FALSE, FALSE, NULL);
    rc = 0;
    for (i = 0; i < pool->size; i++) {
        worker = (worker_t *)malloc(sizeof(worker_t));
        memset(worker, 0, sizeof(worker_t));
        worker->state = WK_IDLE;
        worker->func = thread_pool_internal_callback;
        worker->arg = pool;
        worker->handle = CreateThread(NULL, 0, worker->func, worker, 0, NULL);
        if (worker->handle == NULL) {
            rc = GetLastError();
            break;
        }
        list_add_tail((list_t *)worker, &pool->worker_list);
    }
    if (rc) {
        thread_pool_terminate(pool, 0, 5);
        pool = NULL;
    }
    return pool;
}

void thread_pool_terminate(thread_pool_t *pool, int wait, int timeout)
{
    list_t *temp;
    job_t *job;
    worker_t *worker;
    int tick, finished;

    pool->state = TP_TERMINATING;
    if (!wait) { /* clear job list */
        WaitForSingleObject(pool->job_list_mutex, INFINITE);
        while (!list_empty(&pool->job_list)) {
            job = (job_t *)pool->job_list.next;
            list_del((list_t *)job);
            free(job);
        }
        ReleaseMutex(pool->job_list_mutex);
    } else { /* wait for job list */
        while (1) {
            SetEvent(pool->job_added_event);
            WaitForSingleObject(pool->job_list_mutex, INFINITE);
            if (list_empty(&pool->job_list)) {
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
    /* wait for idle threads */
    tick = timeout;
    while (tick > 0) {
        static const int t = 5;
        Sleep(t*1000);
        tick -= t;
        finished = 1;
        SetEvent(pool->job_added_event);
        temp = pool->worker_list.next;
        while (temp != &pool->worker_list) {
            worker = (worker_t *)temp;
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
    temp = pool->worker_list.next;
    while (temp != &pool->worker_list) {
        worker = (worker_t *)temp;
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
        list_del((list_t *)worker);
        free(worker);
    }
    /* free thread pool */
    CloseHandle(pool->job_list_mutex);
    CloseHandle(pool->job_added_event);
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
    list_add_tail((list_t *)job, &pool->job_list);
    ReleaseMutex(pool->job_list_mutex);
    SetEvent(pool->job_added_event); /* not necessary between mutex lock */
    return 0;
}


#endif
