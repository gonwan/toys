// gcc -O2 libevhtp_server.c  -o libevhtp_server /usr/local/lib/libevhtp.a /usr/local/lib/libevent_core.a /usr/local/lib/libevent_openssl.a -lssl -lcrypto -lpthread
// ~54w/s, libevhtp does not send the server & date headers...
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <errno.h>
#include <unistd.h>
#include <evhtp.h>

static void textcb(evhtp_request_t *req, void *arg)
{
    const char *str = "Hello World!";
    evbuffer_add(req->buffer_out, str, strlen(str));
    evhtp_headers_add_header(req->headers_out, evhtp_header_new("Content-Type", "text/plain", 0, 0));
    evhtp_send_reply(req, EVHTP_RES_OK);
}

static void jsoncb(evhtp_request_t *req, void *arg)
{
    const char *str = "{\"Message\":\"Hello World!\"}";
    evbuffer_add(req->buffer_out, str, strlen(str));
    evhtp_headers_add_header(req->headers_out, evhtp_header_new("Content-Type", "application/json", 0, 0));
    evhtp_send_reply(req, EVHTP_RES_OK);
}

int main()
{
    struct event_base *evbase;
    struct evhtp      *htp;

    evbase = event_base_new();
    htp    = evhtp_new(evbase, NULL);

    evhtp_set_cb(htp, "/text", textcb, NULL);
    evhtp_set_cb(htp, "/json", jsoncb, NULL);
    evhtp_enable_flag(htp, EVHTP_FLAG_ENABLE_ALL);

#ifndef EVHTP_DISABLE_EVTHR
    /* create 1 listener, 4 acceptors */
    evhtp_use_threads_wexit(htp, NULL, NULL, 24, NULL);
#endif

    int port = 8099;
    printf("evhtp server running at %d\n", port);
    evhtp_bind_socket(htp, "0.0.0.0", port, 2048);
    event_base_loop(evbase, 0);

    evhtp_unbind_socket(htp);
    evhtp_safe_free(htp, evhtp_free);
    evhtp_safe_free(evbase, event_base_free);

    return 0;
}
