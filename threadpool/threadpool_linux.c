#ifdef __linux__
#include "threadpool.h"
#include "list.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>


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
    pthread_t pid;
    volatile worker_state_e state;
    thread_func_t func;
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
    list_t *woker_list;
    /* current waiting job list */
    list_t *job_list;
    /* mutex to lock the job list */
    pthread_mutex_t job_list_mutex;
    /* condition used when there is any new job added into the job list */
    pthread_cond_t job_added_condition;
};


static void *thread_pool_internal_callback(void *arg)
{
    worker_t *worker;
    thread_pool_t *pool;
    list_t *jobnode;
    job_t *job;

    worker = (worker_t *)arg;
    pool = (thread_pool_t *)worker->arg;

    while (1) {
        if (pool->state == TP_TERMINATED) {
            worker->state = WK_TERMINATED;
            break;
        }
        /* if job list is not empty, get one */
        job = NULL;
        pthread_mutex_lock(&pool->job_list_mutex);
        while (list_length(pool->job_list) == 0) {
            pthread_cond_wait(&pool->job_added_condition, &pool->job_list_mutex);
        }
        jobnode = pool->job_list;
        job = (job_t *)jobnode->data;
        pool->job_list = list_remove_link(jobnode, jobnode);
        pthread_mutex_unlock(&pool->job_list_mutex);
        /* do not check status, since we are not protected by mutex now */
        if (job) {
            worker->state = WK_RUNNING;
            job->threadfunc(job->arg);
            worker->state = WK_IDLE;
            free(job);
            list_free(jobnode);
        }
    }

    return NULL;
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
    pool->woker_list = NULL;
    pool->job_list = NULL;
    pthread_mutex_init(&pool->job_list_mutex, NULL);
    pthread_cond_init(&pool->job_added_condition, NULL);
    rc = 0;
    for (i = 0; i < pool->size; i++) {
        worker = (worker_t *)malloc(sizeof(worker_t));
        memset(worker, 0, sizeof(worker_t));
        worker->state = WK_IDLE;
        worker->func = thread_pool_internal_callback;
        worker->arg = pool;
        rc = pthread_create(&worker->pid, NULL, worker->func, worker);
        if (rc) {
            worker->pid = 0;
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
        pthread_mutex_lock(&pool->job_list_mutex);
        temp = pool->job_list;
        while (temp) {
            job = (job_t *)temp->data;
            temp = temp->next;
            free(job);
        }
        list_free_all(pool->job_list);
        pool->job_list = NULL;
        pthread_mutex_unlock(&pool->job_list_mutex);
    } else { /* wait for job list */
        while (1) {
            pthread_mutex_lock(&pool->job_list_mutex);
            if (list_length(pool->job_list) == 0) {
                pthread_mutex_unlock(&pool->job_list_mutex);
                break;
            } else {
                pthread_mutex_unlock(&pool->job_list_mutex);
                sleep(1);
            }
        }
    }
    /* now the job list is empty */
    pool->state = TP_TERMINATED;
    /* wait for idle threads */
    tick = timeout;
    while (tick > 0) {
        static const int t = 5;
        sleep(t);
        tick -= t;
        finished = 1;
        /*
         * pthread_cond_broadcast() does nothing if no thread is currently waiting on the condition.
         * So we need to broadcast the terminated state of thread pool in every loop.
         * No mutex is here to protect it, since there's no possibility to change it to another state.
         */
        pthread_cond_broadcast(&pool->job_added_condition);
        temp = pool->woker_list;
        while (temp) {
            worker = (worker_t *)temp->data;
            temp = temp->next;
            if (worker->state != WK_TERMINATED) {
                finished = 0;
            } else {
                if (worker->pid != 0) {
                    finished = 0;
                    pthread_join(worker->pid, NULL);
                    worker->pid = 0;
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
            if (worker->pid != 0) {
#if 0
                fprintf(stderr, "terminating thread %lu\n", worker->pid);
#endif
                /*
                 * Suppose to be safe here.
                 * In implementation of apr and glib, they just abandon the hanging threads.
                 */
                pthread_cancel(worker->pid);
                worker->pid = 0;
            }
        }
        free(worker);
    }
    list_free_all(pool->woker_list);
    pool->woker_list = NULL;
    /* free thread pool */
    pthread_mutex_destroy(&pool->job_list_mutex);
    pthread_cond_destroy(&pool->job_added_condition);
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
    pthread_mutex_lock(&pool->job_list_mutex);
    pool->job_list = list_append(pool->job_list, job);
    pthread_mutex_unlock(&pool->job_list_mutex);
    pthread_cond_signal(&pool->job_added_condition); /* not necessary between mutex lock */
    return 0;
}


#endif
