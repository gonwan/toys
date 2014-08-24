#include <iostream>
#include "function.hpp"
#include "reference_wrapper.hpp"
using namespace std;


void foo0() {
    cout << "foo0()" << endl;
}

void foo1(int) {
    cout << "foo1()" << endl;
}

void foo2(int, int) {
    cout << "foo2()" << endl;
}


struct bar0 {
    void operator()() {
        cout << "bar0()" << endl;
    }
};

struct bar1 {
    void operator()(int) {
        cout << "bar1()" << endl;
    }
};

struct bar2 {
    void operator()(int, int) {
        cout << "bar2()" << endl;
    }
};


struct egg0 {
    void operator()() {
        cout << "egg0()" << endl;
    }
    egg0() { }
private:
    egg0(const egg0 &);
};

struct egg1 {
    void operator()(int) {
        cout << "egg1()" << endl;
    }
    egg1() { }
private:
    egg1(const egg1 &);
};

struct egg2 {
    void operator()(int, int) {
        cout << "egg2()" << endl;
    }
    egg2() { }
private:
    egg2(const egg2 &);
};


struct spam0 {
    void print() {
        cout << "spam0()" << endl;
    }
};

struct spam1 {
    void print(int) {
        cout << "spam1()" << endl;
    }
};

struct spam2 {
    void print(int, int) {
        cout << "spam2()" << endl;
    }
};


void test_function_ptr() {
#if 1
    gl::function0<void> f0 = foo0;
    f0();
    gl::function1<void, int> f1 = foo1;
    f1(1);
    gl::function2<void, int, int> f2 = foo2;
    f2(1, 2);
    gl::function<void()> ff0 = foo0;
    ff0();
    gl::function<void(int)> ff1 = foo1;
    ff1(1);
    gl::function<void(int,int)> ff2 = foo2;
    ff2(1, 2);
    /* leak? */
    f0 = ff0;
    f1 = ff1;
    f2 = ff2;
#endif
}

void test_function_obj() {
#if 1
    gl::function0<void> f0 = bar0();
    f0();
    gl::function1<void, int> f1 = bar1();
    f1(1);
    gl::function2<void, int, int> f2 = bar2();
    f2(1, 2);
    gl::function<void()> ff0 = bar0();
    ff0();
    gl::function<void(int)> ff1 = bar1();
    ff1(1);
    gl::function<void(int,int)> ff2 = bar2();
    ff2(1, 2);
    /* leak? */
    f0 = ff0;
    f1 = ff1;
    f2 = ff2;
#endif
}

void test_function_obj_ref() {
#if 1
    egg0 e0;
    egg1 e1;
    egg2 e2;
    gl::function0<void> f0 = gl::ref(e0);
    f0();
    gl::function1<void, int> f1 = gl::ref(e1);
    f1(1);
    gl::function2<void, int, int> f2 = gl::ref(e2);
    f2(1, 2);
    gl::function<void()> ff0 = gl::ref(e0);
    ff0();
    gl::function<void(int)> ff1 = gl::ref(e1);
    ff1(1);
    gl::function<void(int,int)> ff2 = gl::ref(e2);
    ff2(1, 2);
    /* leak? */
    f0 = ff0;
    f1 = ff1;
    f2 = ff2;
#endif
}

void test_member_funciton_ptr() {
#if 1
    spam0 sp0;
    gl::function1<void, spam0*> f1 = &spam0::print;
    f1(&sp0);
    spam1 sp1;
    gl::function2<void, spam1*, int> f2 = &spam1::print;
    f2(&sp1, 1);
    gl::function<void(spam0*)> ff1 = &spam0::print;
    ff1(&sp0);
    gl::function<void(spam1*,int)> ff2 = &spam1::print;
    ff2(&sp1, 1);
    /* leak? */
    f1 = ff1;
    f2 = ff2;
#endif
}

int main()
{
    test_function_ptr();
    test_function_obj();
    test_function_obj_ref();
    test_member_funciton_ptr();
    return 0;
}
