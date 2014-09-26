#include "common.h"
#include <errno.h>
#include <stdio.h>
#include <string.h>
#include <pthread.h>


int g_total_clients = 0;
int g_client_socks[FD_SETSIZE];


void *worker_thread(void *);

int main()
{
    int rc, server_sock, client_sock;
    struct sockaddr_in server_addr, client_addr;
    pthread_t tid;
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
    printf("== This is Select Server (Linux) ==\n");
    while (1) {
        client_sock = accept(server_sock, (struct sockaddr *)&client_addr, (socklen_t *)&addr_size);
#if 0
        printf("Socket[%d] accepted: %s:%d.\n", client_sock, inet_ntoa(client_addr.sin_addr), ntohs(client_addr.sin_port));
#endif
        g_client_socks[g_total_clients++] = client_sock;
    }
    close(server_sock);
    return 0;
}

void *worker_thread(void *param)
{
    int i, rc;
    fd_set rdset;
    struct timeval tv;
    char msg[MSGSIZE];
    (void)param;

    while (1) {
        /* The tv value is updated every loop under linux, so we need to re-initialize it here. */
        tv.tv_sec = 1;
        tv.tv_usec = 0;
        FD_ZERO(&rdset);
        for (i = 0; i < g_total_clients; i++) {
            FD_SET(g_client_socks[i], &rdset);
        }
        /* The fd count for select() under linux */
        rc = select(g_total_clients+1, &rdset, 0, 0, &tv);
        if (rc == 0) { /* expired */
            continue;
        }
        /* loop over all fds */
        for (i = 0; i < g_total_clients; i++) {
            if (FD_ISSET(g_client_socks[i], &rdset)) {
                memset(msg, 0, MSGSIZE);
                rc = recv(g_client_socks[i], msg, MSGSIZE, 0);
                if (rc == 0 || rc == -1) { /* error */
                    close(g_client_socks[i]);
                    if (i < g_total_clients) {
                        g_client_socks[i--] = g_client_socks[--g_total_clients];
                    }
                } else {
                    printf("Socket[%d] received message: %s.\n", g_client_socks[i], msg);
                }
            }
        }
    }
    return 0;
}
