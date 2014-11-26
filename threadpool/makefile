CC     := gcc
CFLAGS := -pedantic -Wall -Wextra
LIBS   := -lpthread
OBJS   := list.o threadpool_linux.o test_threadpool.o
PROG   := test_threadpool

all: $(PROG)

%.o: %.c
	$(CC) $(CFLAGS) -c $< -o $@

$(PROG): $(OBJS)
	$(CC) $(CFLAGS) $(OBJS) -o $(PROG) $(LIBS)

check:
	grep -n -P '\t' *.h *.c *.md makefile || true
	grep -n -P '\r' *.h *.c *.md makefile || true
	grep -n -P "[\x80-\xFF]" * || true

clean:
	rm -f *~
	rm -f *.o
	rm -f *.bak
	rm -f $(PROG)

