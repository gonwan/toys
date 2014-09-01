## function/bind for c++03

### Feature
- Works like boost::function & boost::bind() in c++03 dialect.
- Supports functions, member functions, function objects & function object reference.
- Supports up to 9 parameters.

### Tested platform
- g++ 4.4/4.6/4.8 on Ubuntu
- VS2005 on Windows XP & VS2013 on Windows 8.1

### gl::function class
```c++
#include <iostream>
#include "function.hpp"

void foo(int a, int b) {
    std::cout << a << ","<< b << std::endl;
}

int main() {
    /* use function2 explicitly */
    gl::function2<void, int, int> f1 = foo;
    f1(1, 2);
    /* with simpler function */
    gl::function<void(int,int)> f2 = foo;
    f2(1, 2);
    return 0;
}
```
Output:
```
1,2
1,2
```
More usage, see: [test_gl_function.cpp](test_gl_function.cpp)

### gl::bind() function
```
#include <iostream>
#include "function.hpp"
#include "bind.hpp"

void foo(int a, int b) {
    std::cout << a << ","<< b << std::endl;
}

int main() {
	/* use placeholders as arguments */
    gl::function<void(int,int)> f1 = gl::bind(foo, _1, _2);
    f1(1, 2);
    gl::function<void(int)> f2 = gl::bind(foo, 3, _1);
    f2(4);
    gl::function<void()> f3 = gl::bind(foo, 5, 6);
    f3();
    return 0;
}
```
Output:
```
1,2
3,4
5,6
```
More usage, see: [test_gl_bind.cpp](test_gl_bind.cpp)
