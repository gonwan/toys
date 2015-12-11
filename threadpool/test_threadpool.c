/*
 * cl threadpool_windows.c test_threadpool.c -o test_threadpool
 * gcc threadpool_linux.c test_threadpool.c -o test_threadpool -lpthread
 */
#include "threadpool.h"
#include <stdio.h>
#include <stdlib.h>
#ifdef _WIN32
#include <windows.h>
#endif
#ifdef __linux__
#include <sys/time.h>
#include <pthread.h>
#include <unistd.h>
#endif


#ifdef _WIN32
#define my_sleep(milsec)        Sleep(milsec)
#define my_current_thread()     GetCurrentThreadId()
unsigned int my_random() {
    unsigned int v;
    FILETIME ft;
    GetSystemTimeAsFileTime(&ft);
    v = (ft.dwLowDateTime*12345) ^ (GetCurrentThreadId()*54321);
    return v % RAND_MAX;
}
#endif
#ifdef __linux__
#define my_sleep(milsec)        usleep(milsec*1000)
#define my_current_thread()     pthread_self()
unsigned int my_random() {
    unsigned int v;
    struct timeval tv;
    gettimeofday(&tv, NULL);
    v = (tv.tv_usec*12345) ^ (pthread_self()*54321);
    return v % RAND_MAX;
}
#endif


void sleep_random()
{
    unsigned int r;
    r = (unsigned int)((double)my_random() / RAND_MAX * 1000*20);

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
