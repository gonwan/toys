#include "common.h"
#include <stdio.h>
#include <string.h>


int g_total_clients = 0;
SOCKET g_client_socks[MAXIMUM_WAIT_OBJECTS];
WSAEVENT g_client_events[MAXIMUM_WAIT_OBJECTS];


DWORD WINAPI WorkerThread(LPVOID);
void Cleanup(int index);

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
    printf("== This is Event Select Server ==\n");
    while (TRUE) {
        client_sock = accept(server_sock, (SOCKADDR *)&client_addr, &addr_size);
#if 0
        printf("Socket[%d] accepted: %s:%d.\n", client_sock, inet_ntoa(client_addr.sin_addr), ntohs(client_addr.sin_port));
#endif
        /* Associate socket with network event */
        g_client_socks[g_total_clients] = client_sock;
        g_client_events[g_total_clients] = WSACreateEvent();
        WSAEventSelect(g_client_socks[g_total_clients], g_client_events[g_total_clients], FD_READ | FD_CLOSE);
        g_total_clients++;
    }
    closesocket(server_sock);
    WSACleanup();
    return 0;
}

DWORD WINAPI WorkerThread(LPVOID lpParam)
{
    int rc, index;
    char msg[MSGSIZE];
    WSANETWORKEVENTS events;

    while (TRUE) {
        rc = WSAWaitForMultipleEvents(g_total_clients, g_client_events, FALSE, 1000, FALSE);
        if (rc == WSA_WAIT_FAILED || rc == WSA_WAIT_TIMEOUT) {
            continue;
        }
        index = rc - WSA_WAIT_EVENT_0;
        WSAEnumNetworkEvents(g_client_socks[index], g_client_events[index], &events);
        if (events.lNetworkEvents & FD_READ) {
            memset(msg, 0, MSGSIZE);
            rc = recv(g_client_socks[index], msg, MSGSIZE, 0);
            if (rc == 0 || (rc == SOCKET_ERROR && WSAGetLastError() == WSAECONNRESET)) { /* error */
                Cleanup(index);
            } else {
                printf("Socket[%d] received message: %s.\n", g_client_socks[index], msg);
            }
        }
        if (events.lNetworkEvents & FD_CLOSE) {
            Cleanup(index);
        }
    }
    return 0;
}

void Cleanup(int index)
{
    closesocket(g_client_socks[index]);
    WSACloseEvent(g_client_events[index]);
    if (index < g_total_clients-1) {
        g_client_socks[index] = g_client_socks[g_total_clients-1];
        g_client_events[index] = g_client_events[g_total_clients-1];
    }
    g_total_clients--;
}
