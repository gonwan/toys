## function/bind for c++11

### Feature
- Works like std::function & std::bind() in c++11 dialect.
- Supports functions, member functions, function objects & function object reference.
- Supports any number of parameters, thanks to the variadic template feature in c++11.

### Tested platform
- g++ 4.6/4.7/4.8 on Ubuntu
- VS2013 on Windows 8.1 (update3 is required)

### gl::function class
```c++
#include <iostream>
#include "function.hpp"

void foo(int a, int b) {
    std::cout << a << ","<< b << std::endl;
}

int main() {
    gl::function<void(int,int)> f2 = foo;
    f2(1, 2);
    return 0;
}
```
Output:
```
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
