#include "bind.hpp"
#include "function.hpp"
#include <iostream>
using namespace std;


struct C {
    int i;
};

void foo(int, const C &) {
}

void foo(int, int, C &c) {
    c.i = 1024;
}

struct c0 {
    void operator()() {
        cout << "c0()" << endl;
    }
};

struct c1 {
    typedef void result_type;
    void operator()(int) const {
        cout << "c1(int)" << endl;
    }
};

struct c2 {
    typedef void result_type;
    c2() { }
    void operator()(int,int) {
        cout << "c2(int,int)" << endl;
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
        cout << "TT::foo2(int)" << endl;
    }
};

void bar(int, double) { }

void test_internal() {
    gl::detail::list1<double> args(1.23);
    gl::detail::list2<gl::detail::value<int>, gl::arg<1> > func(gl::detail::value<int>(1), _1);
    func(gl::detail::type<void>(), bar, args);
}

void test_ref_parameters() {
    C c;
#if __cplusplus >= 201103L
    auto f1 = gl::bind(foo, _1, _2);
    auto f2 = gl::bind(foo, _1, _2, _3);
#else
    //gl::detail::bind_t<void, void(*)(int,const C&), gl::detail::list_helper2<gl::arg<1>, gl::arg<2> >::type> f1 = gl::bind(foo, _1, _2);
    //gl::detail::bind_t<void, void(*)(int,int,C&), gl::detail::list_helper3<gl::arg<1>, gl::arg<2>, gl::arg<3> >::type> f2 = gl::bind(foo, _1, _2, _3);
    gl::function<void(int,const C&)> f1 = gl::bind(foo, _1, _2);
    gl::function<void(int,int,C&)> f2 = gl::bind(foo, _1, _2, _3);
#endif
    f1(1, gl::cref(c));
    c.i = 0;
    f2(1, 2, gl::ref(c));
    cout << c.i << endl;
}

void test_function_obj() {
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
    /* gl::ref() */
    gl::function<void(int,int)> f2 = gl::bind(gl::ref(_c2), _1, _2);
    f2(1, 2);
}

int main()
{
    test_internal();
    test_ref_parameters();
    test_function_obj();
    return 0;
}
