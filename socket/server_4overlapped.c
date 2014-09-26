#include "common.h"
#include <stdio.h>
#include <string.h>


typedef struct {
    WSAOVERLAPPED overlap;
    WSABUF        buffer;
    char          msg[MSGSIZE];
    DWORD         bytes_received;
    DWORD         flags;
} io_operation_data_t;


int g_total_clients = 0;
SOCKET g_client_socks[MAXIMUM_WAIT_OBJECTS];
WSAEVENT g_client_events[MAXIMUM_WAIT_OBJECTS];
io_operation_data_t *g_pio_data_array[MAXIMUM_WAIT_OBJECTS];


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
    printf("== This is Overlapped I/O Server ==\n");
    while (TRUE) {
        client_sock = accept(server_sock, (SOCKADDR *)&client_addr, &addr_size);
#if 0
        printf("Socket[%d] accepted: %s:%d.\n", client_sock, inet_ntoa(client_addr.sin_addr), ntohs(client_addr.sin_port));
#endif
        g_client_socks[g_total_clients] = client_sock;
        /* Associate a io_operation_data_t structure */
        g_pio_data_array[g_total_clients] = (io_operation_data_t *)HeapAlloc(
            GetProcessHeap(),
            HEAP_ZERO_MEMORY,
            sizeof(io_operation_data_t));
        g_pio_data_array[g_total_clients]->buffer.len = MSGSIZE;
        g_pio_data_array[g_total_clients]->buffer.buf = g_pio_data_array[g_total_clients]->msg;
        g_client_events[g_total_clients] = g_pio_data_array[g_total_clients]->overlap.hEvent = WSACreateEvent();
        /* Launch an asynchronous operation */
        memset(g_pio_data_array[g_total_clients]->msg, 0, MSGSIZE);
        WSARecv(g_client_socks[g_total_clients],
            &g_pio_data_array[g_total_clients]->buffer,
            1,
            &g_pio_data_array[g_total_clients]->bytes_received,
            &g_pio_data_array[g_total_clients]->flags,
            &g_pio_data_array[g_total_clients]->overlap,
            NULL);
        g_total_clients++;
    }
    closesocket(server_sock);
    WSACleanup();
    return 0;
}

DWORD WINAPI WorkerThread(LPVOID lpParam)
{
    int rc, index;
    DWORD cbTransferred = 0;

    while (TRUE) {
        rc = WSAWaitForMultipleEvents(g_total_clients, g_client_events, FALSE, 1000, FALSE);
        if (rc == WSA_WAIT_FAILED || rc == WSA_WAIT_TIMEOUT) {
            continue;
        }
        index = rc - WSA_WAIT_EVENT_0;
        WSAResetEvent(g_client_events[index]);
        WSAGetOverlappedResult(g_client_socks[index],
            &g_pio_data_array[index]->overlap,
            &cbTransferred,
            TRUE,
            &g_pio_data_array[g_total_clients]->flags);
        if (cbTransferred == 0) { /* error */
            Cleanup(index);
        } else {
            /* g_pio_data_array[index]->msg contains the received data */
            printf("Socket[%d] received message: %s.\n", g_client_socks[index], g_pio_data_array[index]->msg);
            /* Launch another asynchronous operation */
            memset(g_pio_data_array[index]->msg, 0, MSGSIZE);
            WSARecv(g_client_socks[index],
                &g_pio_data_array[index]->buffer,
                1,
                &g_pio_data_array[index]->bytes_received,
                &g_pio_data_array[index]->flags,
                &g_pio_data_array[index]->overlap,
                NULL);
        }
    }
    return 0;
}

void Cleanup(int index)
{
    closesocket(g_client_socks[index]);
    WSACloseEvent(g_client_events[index]);
    HeapFree(GetProcessHeap(), 0, g_pio_data_array[index]);
    if (index < g_total_clients-1) {
        g_client_socks[index] = g_client_socks[g_total_clients-1];
        g_client_events[index] = g_client_events[g_total_clients-1];
        g_pio_data_array[index] = g_pio_data_array[g_total_clients-1];
    }
    g_pio_data_array[--g_total_clients] = NULL;
}
