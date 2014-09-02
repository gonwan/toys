## function/bind for c++11

### Features
- Works like `std::function` & `std::bind()` in c++11 dialect.
- Supports functions, member functions, function objects & function object reference.
- Supports any number of parameters, thanks to the variadic template feature in c++11.

### Tested platforms
- g++ 4.6/4.7/4.8(-std=c++0x) on Ubuntu
- VS2013(update3 required) on Windows 8.1 

### gl::function class
```c++
#include <iostream>
#include "function.hpp"

void foo(int i, const char *str) {
    std::cout << i << " " << str << std::endl;
}

int main() {
    gl::function<void(int,const char*)> f = foo;
    f(1, "2");
    return 0;
}
```
Output:
```
1 2
```
More usage, see: [test_gl_function.cpp](test_gl_function.cpp)

### gl::bind() function
```c++
#include <iostream>
#include "function.hpp"
#include "bind.hpp"

void foo(int i, const char *str) {
    std::cout << i << " " << str << std::endl;
}

int main() {
    /* use placeholders as arguments */
    gl::function<void(int,const char*)> f0 = gl::bind(foo, _1, _2);
    f0(1, "2");
    gl::function<void(const char*)> f1 = gl::bind(foo, 3, _1);
    f1("4");
    gl::function<void(int)> f2 = gl::bind(foo, _1, "6");
    f2(5);
    gl::function<void()> f3 = gl::bind(foo, 7, "8");
    f3();
    return 0;
}
```
Output:
```
1 2
3 4
5 6
7 8
```
More usage, see: [test_gl_bind.cpp](test_gl_bind.cpp)
