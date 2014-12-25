## thread pool

### Features
- Simple and lightweight thread pool implemented in pure C.

### Tested platforms
- gcc 4.4 on Ubuntu
- VS2005 on Windows XP

### How to use
```c
#include "threadpool.h"
#include <stdio.h>

void *job_callback(void *p)
{
	printf("%d\n", (int)p);
    return NULL;
}

int main()
{
    int i;
    thread_pool_t *pool = thread_pool_create(3);
    for (i = 0; i < 10; i++) {
        thread_pool_add_job(pool, job_callback, (void *)i);
    }
    thread_pool_terminate(pool, 1, 5);
    return 0;
}
```
Output may be:
```
2
3
4
0
6
7
8
9
1
5
```
