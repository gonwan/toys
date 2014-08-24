#include <boost/function.hpp>
#include <boost/bind.hpp>
#include <iostream>
using namespace std;


void a() {
    cout << "a()" << endl;
}

void b() {
    cout << "b()" << endl;
}

void b(int) {
    cout << "b(int)" << endl;
}

void b(int, int) {
    cout << "b(int, int)" << endl;
}

template <typename T1, typename T2>
void b(T1, T2) {
    cout << "b<T1, T2>(...)" << endl;
}

struct c1 {
    void operator()() {
        cout << "c1()" << endl;
    }
};

struct c2 {
    void operator()(int) {
        cout << "c2(int)" << endl;
    }
};

struct c3 {
    void operator()(int, int) {
        cout << "c3(int, int)" << endl;
    }
};

struct TT {
    TT() { }
    void foo() {
        cout << "TT::foo()" << endl;
    }
    void foo2() const {
        cout << "TT::foo2()" << endl;
    }
};

int main()
{
    /* boost::function represents a function object. */
    boost::function<void()> ap = a;
    boost::function<void()> bp1 = (void (*)())b;
    boost::function<void(int)> bp2 = (void (*)(int))b;
    boost::function<void(int,int)> bp3 = (void (*)(int, int))b;
    boost::function<void(int,int)> bp4 = (void (*)(int, int))b<int, int>;
    boost::function<void()> cp1 = c1();
    boost::function<void(int)> cp2 = c2();
    /* use ref to avoid copy */
    c3 t;
    boost::function<void(double,double)> cp3 = boost::ref(t); /* note double is used and implicitly converted to int */
    cp3(1.0, 2.0);
    /* member functions */
    TT tt;
    boost::function<void()> m1 = boost::bind(&TT::foo, &tt);
    //boost::function0<void> m2 = &TT::foo; /* not working */
    boost::function1<void, TT *> m3 = &TT::foo;
#if __cplusplus >= 201103L
    /* mem_fn */
    TT t1;
    const TT t2;
    auto _m = boost::mem_fn(&TT::foo);
    auto _mc = boost::mem_fn(&TT::foo2);
    _m(&t1);
    //_m(&t2); /* not working */
    _m(t1);
    //_m(t2); /* not working */
    _mc(&t1);
    _mc(&t2);
    _mc(t1);
    _mc(t2);
#endif
    return 0;
}
