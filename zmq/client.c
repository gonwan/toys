#include <zmq.h>
#include <stdio.h>
#include <string.h>

int main(int argc, char *argv[]) {
    void *context = zmq_ctx_new();
    void *socket = zmq_socket(context, ZMQ_SUB);
    zmq_connect(socket, "tcp://127.0.0.1:5555");
    const char *subscription = (argc < 2) ? NULL : argv[1];
    if (subscription == NULL) {
        zmq_setsockopt(socket, ZMQ_SUBSCRIBE, "", 0);
    } else {
        zmq_setsockopt(socket, ZMQ_SUBSCRIBE, argv[1], strlen(argv[1]));
    }
    while (1) {
    	char prefix[128] = { 0 };
    	char message[128] = { 0 };
    	if (subscription == NULL) {
    		zmq_recv(socket, message, sizeof(message)-1, 0);
        	printf("Received message: %s\n", message);
    	} else {
        	zmq_recv(socket, prefix, sizeof(prefix)-1, 0);
    		zmq_recv(socket, message, sizeof(message)-1, 0);
        	printf("Received message from [%s]: %s\n", prefix, message);
    	}
    }
    zmq_close(socket);
    zmq_ctx_destroy(context);
    return 0;
}

