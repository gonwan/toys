#include "common.h"
#include <errno.h>
#include <stdio.h>
#include <string.h>
#include <pthread.h>
#include <sys/epoll.h>


#define MAX_EVENTS 10


static int g_epollfd = 0;
struct epoll_event g_events[MAX_EVENTS];


void *worker_thread(void *);

int main()
{
    int rc, server_sock, client_sock;
    struct sockaddr_in server_addr, client_addr;
    pthread_t tid;
    struct epoll_event ev;
    int addr_size = sizeof(struct sockaddr_in);

    server_sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = htonl(INADDR_ANY);
    server_addr.sin_port = htons(SERVER_PORT);
    rc = bind(server_sock, (struct sockaddr *)&server_addr, sizeof(struct sockaddr_in));
    if (rc < 0) {
        fprintf(stderr, "bind: %s\n", strerror(errno));
        return -1;
    }
    listen(server_sock, 3);
    /* Create worker thread */
    pthread_create(&tid, 0, worker_thread, 0);
    pthread_detach(tid);
    printf("== This is EPoll Server (Linux) ==\n");
    g_epollfd = epoll_create(MAX_EVENTS);
    while (1) {
        client_sock = accept(server_sock, (struct sockaddr *)&client_addr, (socklen_t *)&addr_size);
#if 0
        printf("Socket[%d] accepted: %s:%d.\n", client_sock, inet_ntoa(client_addr.sin_addr), ntohs(client_addr.sin_port));
#endif
        ev.data.fd = client_sock;
        ev.events = EPOLLIN | EPOLLET;
        epoll_ctl(g_epollfd, EPOLL_CTL_ADD, client_sock, &ev);
    }
    close(server_sock);
    return 0;
}

void *worker_thread(void *param)
{
    int i, fds, rc;
    char msg[MSGSIZE];
    (void)param;
    while (1) {
        fds = epoll_wait(g_epollfd, g_events, MAX_EVENTS, -1);
        if (fds == 0) { /* expired */
            continue;
        }
        /* loop over limited fds */
        for (i = 0; i < fds; i++) {
            if (g_events[i].events == EPOLLIN) {
                memset(msg, 0, MSGSIZE);
                rc = recv(g_events[i].data.fd, msg, MSGSIZE, 0);
                if (rc == 0 || rc == -1) { /* error */
                    epoll_ctl(g_epollfd, EPOLL_CTL_DEL, g_events[i].data.fd, 0);
                    close(g_events[i].data.fd);
                } else {
                    printf("Socket[%d] received message: %s.\n", g_events[i].data.fd, msg);
                }
            }
        }
    }
    return 0;
}
