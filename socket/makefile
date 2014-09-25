CC     := gcc
CFLAGS := -pedantic -Wall -Wextra

all: cli select poll epoll 

cli:
	$(CC) $(CFLAGS) client.c -o client

select:
	$(CC) $(CFLAGS) server_6linuxselect.c -o server_6linuxselect -lpthread

poll:
	$(CC) $(CFLAGS) server_7linuxpoll.c -o server_7linuxpoll -lpthread

epoll:
	$(CC) $(CFLAGS) server_8linuxepoll.c -o server_8linuxepoll -lpthread

check:
	grep -n -P '\t' *.h *.c *.md makefile || true
	grep -n -P '\r' *.h *.c *.md makefile || true
	grep -n -P "[\x80-\xFF]" * || true

clean:
	rm -rf client
	rm -rf server_6linuxselect
	rm -rf server_7linuxpoll
	rm -rf server_8linuxepoll
