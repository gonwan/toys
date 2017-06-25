#include <zmq.h>
#include <stdio.h>
#include <string.h>
#ifdef _WIN32
#include <windows.h>
#else
#include <unistd.h>
#endif

int main(int argc, char *argv[]) {
    void *context = zmq_ctx_new();
    void *socket = zmq_socket(context, ZMQ_PUB);
    zmq_bind(socket, "tcp://*:5555");
    int c = 0;
    while (1) {
        char message[128] = { 0 };
        sprintf(message, "Hello %d", c);
        if (argc == 1) {
            zmq_send(socket, message, strlen(message), 0);
            printf("Sent message: %s\n", message);
        } else {
            int i = c % (argc - 1);
            char *prefix = argv[i + 1];
            zmq_send(socket, prefix, strlen(prefix), ZMQ_SNDMORE);
            zmq_send(socket, message, strlen(message), 0);
            printf("Sent message to [%s]: %s\n", prefix, message);
        }
#ifdef _WIN32
        Sleep(1000);
#else
        sleep(1);
#endif
        c++;
    }
    zmq_close(socket);
    zmq_ctx_destroy(context);
    return 0;
}

