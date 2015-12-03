#ifndef _THREADPOOL_H
#define _THREADPOOL_H


#include <stddef.h>


/* forward declaration */
typedef struct _thread_pool_t thread_pool_t;
/* thread callback function */
typedef void *(*thread_func_t)(void *arg);


/**
 * Create thread pool.
 * @param size number of threads to create in the thread pool
 * @return newly created thread pool, or NULL if fails
 */
thread_pool_t *thread_pool_create(/*in*/ size_t size);
/**
 * Terminate thread pool.
 * @param pool the thread pool to terminate
 * @param wait whether to wait all jobs to finish in the job list
 * @param timeout additional waiting timeout after job list becomes empty
 */
void thread_pool_terminate(/*in*/ thread_pool_t *pool, /*in*/ int wait, /*in*/ int timeout);
/**
 * Add job to thread pool.
 * It's your responsibility to control memory usage in the arg parameter.
 * @param pool the thread pool to which we add job
 * @param func callback function to add
 * @param arg argument to the callback function
 * @return 0 if ok, -1 otherwise
 */
int thread_pool_add_job(/*in*/ thread_pool_t *pool, /*in*/ thread_func_t func, /*in*/ void *arg);


#endif
