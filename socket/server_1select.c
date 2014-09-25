#include "common.h"
#include <stdio.h>
#include <string.h>


int g_total_clients = 0;
SOCKET g_client_socks[FD_SETSIZE];


DWORD WINAPI WorkerThread(LPVOID);

int main()
{
    WSADATA wsaData;
    SOCKET server_sock, client_sock;
    SOCKADDR_IN server_addr, client_addr;
    DWORD tid;
    int addr_size = sizeof(SOCKADDR_IN);

    WSAStartup(0x0202, &wsaData);
    server_sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = htonl(INADDR_ANY);
    server_addr.sin_port = htons(SERVER_PORT);
    bind(server_sock, (SOCKADDR *)&server_addr, sizeof(SOCKADDR_IN));
    listen(server_sock, 3);
    /* Create worker thread */
    CreateThread(NULL, 0, WorkerThread, NULL, 0, &tid);
    printf("== This is Select Server ==\n");
    while (TRUE) {
        client_sock = accept(server_sock, (SOCKADDR *)&client_addr, &addr_size);
#if 0
        printf("Socket[%d] accepted: %s:%d.\n", client_sock, inet_ntoa(client_addr.sin_addr), ntohs(client_addr.sin_port));
#endif
        g_client_socks[g_total_clients++] = client_sock;
    }
    closesocket(server_sock);
    WSACleanup();
    return 0;
}

DWORD WINAPI WorkerThread(LPVOID lpParam)
{
    int i, rc;
    fd_set rdset;
    char msg[MSGSIZE];
    struct timeval tv = { 1, 0 };

    while (TRUE) {
        FD_ZERO(&rdset);
        for (i = 0; i < g_total_clients; i++) {
            FD_SET(g_client_socks[i], &rdset);
        }
        rc = select(0, &rdset, NULL, NULL, &tv);
        if (rc == 0) { /* expired */
            continue;
        }
        /* loop over all fds */
        for (i = 0; i < g_total_clients; i++) {
            if (FD_ISSET(g_client_socks[i], &rdset)) {
                memset(msg, 0, MSGSIZE);
                rc = recv(g_client_socks[i], msg, MSGSIZE, 0);
                if (rc == 0 || (rc == SOCKET_ERROR && WSAGetLastError() == WSAECONNRESET)) { /* error */
                    closesocket(g_client_socks[i]);
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
