#include "bind.hpp"
#include "function.hpp"
#include <iostream>
using namespace std;


struct C {
    int i;
    C() : i(0) { }
};

void foo(int, C &c) {
    c.i = 1024;
}

void foo(int, int, const C &) {
}

void foo2(int, int, const C &) {
}

void foo3(int, C &&) {
}

struct c0 {
    void operator()() {
        cout << "c0()" << endl;
    }
};

struct c1 {
    typedef void result_type;
    void operator()(int) const {
        cout << "c1(int)const" << endl;
    }
};

struct c2 {
    typedef void result_type;
    c2() { }
    void operator()(int,int) {
        cout << "c2(int,int)" << endl;
    }
    void operator()(int,int) const {
        cout << "c2(int,int)const" << endl;
    }
private:
    c2(const c2&);
};

struct TT {
    TT() { }
    void foo() {
        cout << "TT::foo()" << endl;
    }
    void foo2(int) const {
        cout << "TT::foo2(int)const" << endl;
    }
};

void bar(int, double) { }

void test_internal() {
#if 1
    gl::detail::list<double> args(1.23);
    gl::detail::list<gl::detail::value<int>, gl::arg<1>& > func(gl::detail::value<int>(1), _1);
    func(gl::detail::type<void>(), bar, args);
#endif
}

void test_ref_parameters() {
#if 1
    C c;
    const C c2;
#if 0
    auto f1 = gl::bind((void(*)(int,C&))foo, _1, _2);
    auto f2 = gl::bind((void(*)(int,int,const C&))foo, _1, _2, _3);
    f2 = gl::bind(foo2, _1, _2, _3);
#else
    /* const lvalue reference */
    gl::function<void(int,int,const C&)> f20 = gl::bind((void(*)(int,int,const C&))foo, _1, _2, _3);
    f20(1, 2, c2);
    gl::function<void(int,int)> f21 = gl::bind((void(*)(int,int,const C&))foo, _1, _2, c2);
    f21(1, 2);
    gl::function<void(int,const C&)> f22 = gl::bind((void(*)(int,int,const C&))foo, _1, 1, _2);
    f22(1, c2);
    /* lvalue reference */
    gl::function<void(int,C&)> f10 = gl::bind((void(*)(int,C&))foo, _1, _2);
    f10(1, c);
    cout << c.i << endl;
    gl::function<void(C&)> f11 = gl::bind((void(*)(int,C&))foo, 1, _1);
    f11(c);
    gl::function<void(int)> f12 = gl::bind((void(*)(int,C&))foo, _1, c);
    f12(1);
    gl::function<void()> f13 = gl::bind((void(*)(int,C&))foo, 1, c);
    f13();
#endif
    /*
     * rvalue reference
     * Assignment to gl::function is not supported, see function.hpp.
     */
    //gl::function<void(int,C&&)> f30 = gl::bind(foo3, _1, _2);
    //gl::function<void(C&&)> f31 = gl::bind(foo3, 1, _1);
    //gl::function<void(int)> f32 = gl::bind(foo3, _1, std::move(c));
    //gl::function<void()> f33 = gl::bind(foo3, 1, std::move(c));
    auto f30 = gl::bind(foo3, _1, _2);
    auto f31 = gl::bind(foo3, 1, _1);
    auto f32 = gl::bind(foo3, _1, std::move(c));
    auto f33 = gl::bind(foo3, 1, std::move(c));
    f30(1, std::move(c));
    f31(std::move(c));
    f32(1);
    f33();
#endif
}

void test_function_obj() {
#if 1
    c0 _c0;
    c1 _c1;
    c2 _c2;
    /* specify return type explicitly */
    gl::function<void()> f0 = gl::bind<void>(_c0);
    f0();
    /* do not specify return type */
    gl::function<void(int)> f10 = gl::bind(_c1, _1);
    f10(1);
    gl::function<void()> f11 = gl::bind(_c1, 1);
    f11();
    /* gl::ref() and gl::cref() */
    gl::function<void(int,int)> f2 = gl::bind(gl::ref(_c2), _1, _2);
    f2(1, 2);
    gl::function<void(int,int)> f2c = gl::bind(gl::cref(_c2), _1, _2);
    f2c(1, 2);
#endif
}

void test_member_function() {
#if 1
    TT t1;
    const TT t2;
    gl::function<void(TT*)> f10 = gl::bind(&TT::foo, _1);
    f10(&t1);
    gl::function<void()> f11 = gl::bind(&TT::foo, &t1);
    f11();
    gl::function<void(const TT*,int)> f20 = gl::bind(&TT::foo2, _1, _2);
    f20(&t2, 1);
    gl::function<void(const TT*)> f21 = gl::bind(&TT::foo2, _1, 1);
    f21(&t2);
    gl::function<void(int)> f22 = gl::bind(&TT::foo2, &t2, _1);
    f22(1);
    gl::function<void()> f23 = gl::bind(&TT::foo2, &t2, 1);
    f23();
#endif
}

void f(int a, int b, int c, int d) {
    cout << a << ", " << b << ", " << c << ", " << d << endl;
}

int g(int i) {
    return i;
}

void test_misc() {
#if 1
    gl::function<void(int,int,int,int)> f1 = gl::bind(f, _3, _4, 3, 4);
    f1(1001, 1002, 1003, 1004); /* only 1003 and 1004 is used */
    gl::function<void(int,int,int,int)> f2 = gl::bind(f, _3, gl::bind(g, _3), 5, 6);
    f2(1001, 1002, 1003, 1004); /* nested bind subexpressions share the placeholders */
#endif
}

int main()
{
    test_internal();
    test_ref_parameters();
    test_function_obj();
    test_member_function();
    test_misc();
    return 0;
}
