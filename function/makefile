CC     := g++
CFLAGS := -pedantic -Wall -Wextra
OBJS   := test_gl_function.o
LIBS   := -lstdc++
PROG   := test_gl_function

all: $(PROG)

%.o: %.cpp
	$(CC) $(CFLAGS) -c $< -o $@

$(PROG): $(OBJS)
	$(CC) $(CFLAGS) $(OBJS) -o $(PROG) $(LIBS)

check:
	grep -n -P '\t' *.hpp *.cpp *.txt makefile || true
	grep -n -P '\r' *.hpp *.cpp *.txt makefile || true
	grep -n -P "[\x80-\xFF]" * || true

clean:
	rm -f *~
	rm -f *.o
	rm -f *.bak
	rm -f $(PROG)
