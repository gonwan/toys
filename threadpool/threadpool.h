#ifndef _THREAD_H
#define _THREAD_H


/* thread callbacks */
typedef int (*thread_mutex_callback)(void **lock);
typedef unsigned long (*thread_id_callback)(void);
typedef struct _thread_callbacks_t {
    const char *type;
    thread_mutex_callback mutex_init;
    thread_mutex_callback mutex_destroy;
    thread_mutex_callback mutex_lock;
    thread_mutex_callback mutex_unlock;
    thread_id_callback thread_id;
} thread_callbacks_t;

/**
 * Get native thread callbacks of each platform.
 * @return thread callbacks
 */
thread_callbacks_t *thread_get_callbacks();


/* forward declaration */
typedef struct _thread_pool_t thread_pool_t;
/* thread callback function */
typedef void *(*thread_func_t)(void *arg);


/**
 * Create thread pool.
 * @param max maximum threads to create in the thread pool
 * @return newly created thread pool, or NULL if fails
 */
thread_pool_t *thread_pool_create(/*in*/ int max);
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
