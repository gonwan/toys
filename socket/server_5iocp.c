#include "common.h"
#include <stdio.h>
#include <string.h>


typedef struct {
    WSAOVERLAPPED overlap;
    WSABUF        buffer;
    char          msg[MSGSIZE];
    DWORD         bytes_received;
    DWORD         flags;
} PER_IO_OPERATION_DATA, *LPPER_IO_OPERATION_DATA;


DWORD WINAPI WorkerThread(LPVOID comp_port_id);

int main()
{
    WSADATA wsaData;
    SOCKET server_sock, client_sock;
    SOCKADDR_IN server_addr, client_addr;
    DWORD i, tid;
    HANDLE comp_port;
    SYSTEM_INFO system_info;
    LPPER_IO_OPERATION_DATA pper_io_data;
    int addr_size = sizeof(SOCKADDR_IN);

    WSAStartup(0x0202, &wsaData);
    server_sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = htonl(INADDR_ANY);
    server_addr.sin_port = htons(SERVER_PORT);
    bind(server_sock, (SOCKADDR *)&server_addr, sizeof(SOCKADDR_IN));
    listen(server_sock, 3);
    /* Create IO completion port */
    comp_port = CreateIoCompletionPort(INVALID_HANDLE_VALUE, NULL, 0, 0);
    /* Create worker thread */
    GetSystemInfo(&system_info);
    for (i = 0; i < system_info.dwNumberOfProcessors; i++) {
        CreateThread(NULL, 0, WorkerThread, comp_port, 0, &tid);
    }
    printf("== This is I/O Completion Port Server ==\n");
    while (TRUE) {
        client_sock = accept(server_sock, (SOCKADDR *)&client_addr, &addr_size);
#if 0
        printf("Socket[%d] accepted: %s:%d.\n", client_sock, inet_ntoa(client_addr.sin_addr), ntohs(client_addr.sin_port));
#endif
        /* Associate the newly arrived client socket with completion port */
        CreateIoCompletionPort((HANDLE)client_sock, comp_port, (DWORD)client_sock, 0);
        /* Associate a PER_IO_OPERATION_DATA structure */
        pper_io_data = (LPPER_IO_OPERATION_DATA)HeapAlloc(
            GetProcessHeap(),
            HEAP_ZERO_MEMORY,
            sizeof(PER_IO_OPERATION_DATA));
        pper_io_data->buffer.len = MSGSIZE;
        pper_io_data->buffer.buf = pper_io_data->msg;
        /* Launch an asynchronous operation */
        memset(pper_io_data->msg, 0, MSGSIZE);
        WSARecv(client_sock,
            &pper_io_data->buffer,
            1,
            &pper_io_data->bytes_received,
            &pper_io_data->flags,
            &pper_io_data->overlap,
            NULL);
    }
    PostQueuedCompletionStatus(comp_port, 0xffffffff, 0, NULL);
    CloseHandle(comp_port);
    closesocket(server_sock);
    WSACleanup();
    return 0;
}

DWORD WINAPI WorkerThread(LPVOID lpParam)
{
    DWORD cbTransferred;
    SOCKET client_sock;
    LPPER_IO_OPERATION_DATA pper_io_data;
    HANDLE comp_port = (HANDLE)lpParam;

    while (TRUE) {
        GetQueuedCompletionStatus(
            comp_port,
            &cbTransferred,
            (DWORD *)&client_sock,
            (LPOVERLAPPED *)&pper_io_data,
            INFINITE);
        if (cbTransferred == 0xffffffff) {
            return 0;
        }
        if (cbTransferred == 0) { /* error */
            closesocket(client_sock);
            HeapFree(GetProcessHeap(), 0, pper_io_data);
        } else {
            /* pper_io_data->msg contains the received data */
            printf("Socket[%d] received message: %s.\n", client_sock, pper_io_data->msg);
            send(client_sock, pper_io_data->msg, cbTransferred, 0);
            /* Launch another asynchronous operation */
            memset(pper_io_data, 0, sizeof(PER_IO_OPERATION_DATA));
            pper_io_data->buffer.len = MSGSIZE;
            pper_io_data->buffer.buf = pper_io_data->msg;
            WSARecv(client_sock,
                &pper_io_data->buffer,
                1,
                &pper_io_data->bytes_received,
                &pper_io_data->flags,
                &pper_io_data->overlap,
                NULL);
        }
    }
    return 0;
}
