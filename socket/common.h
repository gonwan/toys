#ifndef _COMMON_H
#define _COMMON_H


#define SERVER_ADDR     "127.0.0.1"
#define SERVER_PORT     7777
#define MSGSIZE         128


#ifdef _WIN32
#include <winsock2.h>
#include <windows.h>
#pragma comment(lib, "ws2_32")
#pragma warning(disable: 4996)
#define my_sleep(milsec)            Sleep(milsec)
#define sock_send(s, str, len)      send(s, str, len, 0)
#define sock_close(s)               closesocket(s)
#endif
#ifdef __linux__
#include <unistd.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/types.h>
#include <sys/socket.h>
#define my_sleep(milsec)            usleep(milsec*1000)
/* linux will generate SIGPIPE when remote socket is closed, ignore this. */
#define sock_send(s, str, len)      send(s, str, len, MSG_NOSIGNAL)
#define sock_close(s)               close(s)
#endif


#endif
