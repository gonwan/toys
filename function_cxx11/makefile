CC     := g++
CFLAGS := -std=c++0x -pedantic -Wall -Wextra
LIBS   := -lstdc++
PROG1  := test_gl_function
PROG2  := test_gl_bind
OBJS1  := $(PROG1).o
OBJS2  := $(PROG2).o

all: $(PROG1)

%.o: %.cpp
	$(CC) $(CFLAGS) -c $< -o $@

$(PROG1): $(OBJS1)
	$(CC) $(CFLAGS) $(OBJS1) -o $(PROG1) $(LIBS)

check:
	grep -n -P '\t' *.hpp *.cpp *.txt makefile || true
	grep -n -P '\r' *.hpp *.cpp *.txt makefile || true
	grep -n -P "[\x80-\xFF]" * || true

clean:
	rm -f *~
	rm -f *.o
	rm -f *.bak
	rm -f $(PROG1)
