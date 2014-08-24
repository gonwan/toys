#include <boost/bind.hpp>
#include <boost/function.hpp>
#include <iostream>
using namespace std;


/**
 * 0. bind.hpp        - for function obj
 * 1. bind_cc.hpp     - for function ptr
 * 2. bind_mf_cc.hpp  - for member function ptr
 * 3. bind_mf2_cc.hpp - for member function ptr, but can customize return type
 */


class C { };

void foo(int, C &) { }

void foo(int, int, C &) { }

struct c0 {
    void operator()() {
        cout << "c0()" << endl;
    }
};

struct c1 {
    void operator()(int) {
        cout << "c1(int)" << endl;
    }
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


void test_bug() {
#if __cplusplus >= 201103L
    C c;
    /* working with 2 parameters */
    auto f1 = boost::bind(foo, _1, _2);
    f1(1, c);
    auto f2 = boost::bind(foo, _1, _2, _3);
#if _BUG
    /* not working with 3 parameters?? */
    /* but working with stl of gcc-4.6 and gcc-4.8 */
    f2(1, 2, c);
#else
    /* wrap with ref() to correct this */
    f2(1, 2, boost::ref(c));
    /* or...... */
    f2.operator()<int,int,C&>(1, 2, c);
#endif
#endif
}

void test_function_obj() {
    c0 _c0;
    c1 _c1;
    /* return value is required explicitly for function objects */
    boost::function<void()> f00 = boost::bind<void>(_c0);
    f00();
    boost::function<void(int)> f10 = boost::bind<void>(_c1, _1);
    f10(1);
    boost::function<void()> f11 = boost::bind<void>(_c1, 1);
    f11();
}

int main()
{
    test_bug();
    test_function_obj();
    return 0;
}
