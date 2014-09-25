#include "common.h"
#include <stdio.h>
#include <string.h>


#define WM_SOCKET       WM_USER + 100


LRESULT CALLBACK WndProc(HWND, UINT, WPARAM, LPARAM);

int main()
{
    static char szAppName[] = "AsyncSelect Model";
    HWND hwnd;
    MSG msg;
    WNDCLASSA wndclass;

    wndclass.style         = CS_HREDRAW | CS_VREDRAW;
    wndclass.lpfnWndProc   = WndProc;
    wndclass.cbClsExtra    = 0;
    wndclass.cbWndExtra    = 0;
    wndclass.hInstance     = NULL;
    wndclass.hIcon         = LoadIcon(NULL, IDI_APPLICATION);
    wndclass.hCursor       = LoadCursor(NULL, IDC_ARROW);
    wndclass.hbrBackground = (HBRUSH)GetStockObject(WHITE_BRUSH);
    wndclass.lpszMenuName  = NULL;
    wndclass.lpszClassName = szAppName;
    if (!RegisterClassA(&wndclass)) {
        MessageBoxA(NULL, "This program requires Windows NT!", szAppName, MB_ICONERROR);
        return 0;
    }
    hwnd = CreateWindowA(szAppName, /* window class name */
        szAppName,                  /* window caption */
        WS_OVERLAPPEDWINDOW,        /* window style */
        CW_USEDEFAULT,              /* initial x position */
        CW_USEDEFAULT,              /* initial y position */
        CW_USEDEFAULT,              /* initial x size */
        CW_USEDEFAULT,              /* initial y size */
        NULL,                       /* parent window handle */
        NULL,                       /* window menu handle */
        NULL,                       /* program instance handle */
        NULL);                      /* creation parameters */
    ShowWindow(hwnd, SW_HIDE);
    while (GetMessage(&msg, NULL, 0, 0)) {
        TranslateMessage(&msg);
        DispatchMessage(&msg);
    }
    return (int)msg.wParam;
}

LRESULT CALLBACK WndProc(HWND hwnd, UINT message, WPARAM wParam, LPARAM lParam)
{
    WSADATA wsaData;
    static SOCKET server_sock;
    SOCKET client_sock;
    SOCKADDR_IN server_addr, client_addr;
    int rc, addr_size;
    char msg[MSGSIZE];
    addr_size = sizeof(client_addr);

    switch (message)
    {
    case WM_CREATE:
        WSAStartup(0x0202, &wsaData);
        server_sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
        server_addr.sin_family = AF_INET;
        server_addr.sin_addr.s_addr = htonl(INADDR_ANY);
        server_addr.sin_port = htons(SERVER_PORT);
        bind(server_sock, (SOCKADDR *)&server_addr, sizeof(SOCKADDR_IN));
        listen(server_sock, 3);
        printf("== This is Async Select Server ==\n");
        /* Associate listening socket with FD_ACCEPT event */
        WSAAsyncSelect(server_sock, hwnd, WM_SOCKET, FD_ACCEPT);
        return 0;
    case WM_DESTROY:
        closesocket(server_sock);
        WSACleanup();
        PostQuitMessage(0);
        return 0;
    case WM_SOCKET:
        if (WSAGETSELECTERROR(lParam)) {
            closesocket(wParam);
            break;
        }
        switch (WSAGETSELECTEVENT(lParam))
        {
        case FD_ACCEPT:
            client_sock = accept(wParam, (SOCKADDR *)&client_addr, &addr_size);
#if 0
            printf("Socket[%d] accepted: %s:%d.\n", client_sock, inet_ntoa(client_addr.sin_addr), ntohs(client_addr.sin_port));
#endif
            /* Associate client socket with FD_READ and FD_CLOSE event */
            WSAAsyncSelect(client_sock, hwnd, WM_SOCKET, FD_ALL_EVENTS /*FD_READ | FD_CLOSE*/);
            break;
        case FD_READ:
            memset(msg, 0, MSGSIZE);
            rc = recv(wParam, msg, MSGSIZE, 0);
            if (rc == 0 || (rc == SOCKET_ERROR && WSAGetLastError() == WSAECONNRESET)) { /* error */
                closesocket(wParam);
            } else {
                printf("Socket[%d] received message: %s.\n", wParam, msg);
            }
            break;
        case FD_CLOSE:
            closesocket(wParam);
            break;
        }
        return 0;
    }
    return DefWindowProc(hwnd, message, wParam, lParam);
}
