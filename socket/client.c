#include "common.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>


#define MAX_CLIENT  8


int main()
{
    int i, client_socks[MAX_CLIENT];
    struct sockaddr_in server_addr;
    char msg[32];
#ifdef WIN32
    WSADATA wsaData;
    WSAStartup(0x0202, &wsaData);
#endif

    srand((unsigned)time(0));
    memset(&server_addr, 0, sizeof(struct sockaddr_in));
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = inet_addr(SERVER_ADDR);
    server_addr.sin_port = htons(SERVER_PORT);
    memset(client_socks, 0xff, sizeof(client_socks)); /* -1 */
    /* simulate multi-threading here, to simply code :) */
    while (1) {
        for (i = 0; i < MAX_CLIENT; i++) {
            int r = rand() % MAX_CLIENT;
            if (client_socks[r] != -1) {
                sock_close(client_socks[r]);
            }
            client_socks[r] = (int)socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
            if (client_socks[r] != -1) {
                connect(client_socks[r], (struct sockaddr *)&server_addr, sizeof(struct sockaddr_in));
                memset(msg, 0, sizeof(msg));
                sprintf(msg, "from client socket[%d]", client_socks[r]);
                printf("Client socket send: %s.\n", msg);
                sock_send(client_socks[r], msg, (int)strlen(msg));
            }
        }
        my_sleep(1000);
    }
    for (i = 0; i < MAX_CLIENT; i++) {
        if (client_socks[i] != -1) {
            sock_close(client_socks[i]);
        }
    }
#ifdef WIN32
    WSACleanup();
#endif
    return 0;
}
