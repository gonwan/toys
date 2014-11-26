/*
 * cl list.c threadpool_windows.c test_threadpool.c -o test_threadpool
 * gcc list.c threadpool_linux.c test_threadpool.c -o test_threadpool -lpthread
 */
#include "threadpool.h"
#include <stdio.h>
#define _CRT_RAND_S
#include <stdlib.h>
#ifdef _WIN32
#include <windows.h>
#endif
#ifdef __linux__
#include <unistd.h>
#include <pthread.h>
#endif


#ifdef _WIN32
#define my_sleep(milsec)        Sleep(milsec)
#define my_current_thread()     GetCurrentThreadId()
#endif
#ifdef __linux__
#define my_sleep(milsec)        usleep(milsec*1000)
#define my_current_thread()     pthread_self()
#endif


void sleep_random()
{
    unsigned int r;

#ifdef _WIN32
    rand_s(&r);
    r = (int)((double)r / UINT_MAX * 1000*20);
#endif
#ifdef __linux__
    r = random();
    r = (int)((double)r / RAND_MAX * 1000*20);
#endif
    printf("[%lu] start sleep %u msec\n", my_current_thread(), r);
    my_sleep(r);
    printf("[%lu] end sleep %u msec\n", my_current_thread(), r);
}

void *job_callback(void *p)
{
    (void)p;
    sleep_random();
    return NULL;
}

int main()
{
    int i;
    thread_pool_t *pool = thread_pool_create(4);
    for (i = 0; i < 10; i++) {
        thread_pool_add_job(pool, job_callback, NULL);
    }
    my_sleep(5000);
    thread_pool_terminate(pool, 1, 30);
    return 0;
}
