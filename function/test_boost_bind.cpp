#include <boost/bind.hpp>
#include <boost/function.hpp>
#include <iostream>
using namespace std;


struct C {
    int i;
};

void foo(int, C &) {
}

void foo(int, int, C &c) {
    c.i = 1024;
}

void foo(int, int, int, const C&) {
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
    boost::_bi::list1<double> args(1.23);
    boost::_bi::list2<boost::_bi::value<int>, boost::arg<1> > func(boost::_bi::value<int>(1), _1);
    func(boost::_bi::type<void>(), bar, args, 0);
}

void test_bug() {
    C c;
#if __cplusplus >= 201103L
    /* working with 2 parameters */
    auto f1 = boost::bind(foo, _1, _2);
    f1(1, c);
    auto f2 = boost::bind(foo, _1, _2, _3);
#else
    /* working with 2 parameters */
    boost::_bi::bind_t<void, void(*)(int,C&), boost::_bi::list_av_2<boost::arg<1>, boost::arg<2> >::type> f1 = boost::bind(foo, _1, _2);
    f1(1, c);
    boost::_bi::bind_t<void, void(*)(int,int,C&), boost::_bi::list_av_3<boost::arg<1>, boost::arg<2>, boost::arg<3> >::type> f2 = boost::bind(foo, _1, _2, _3);
#endif
#if _BUG
    /* not working with 3 parameters?? */
    /* but working with stl of gcc-4.6 and gcc-4.8 */
    f2(1, 2, c);
#else
    /* wrap with ref() to correct this */
    c.i = 0;
    f2(1, 2, boost::ref(c));
    cout << c.i << endl;
    /* or...... */
    c.i = 0;
    f2.operator()<int,int,C&>(1, 2, c);
    cout << c.i << endl;
#endif
}

void test_bug2() {
    C c;
#if __cplusplus >= 201103L
    auto f = boost::bind(foo, _1, _2, _3, _4);
#else
    boost::_bi::bind_t<void, void(*)(int,int,int,const C&), boost::_bi::list_av_4<boost::arg<1>, boost::arg<2>, boost::arg<3>, boost::arg<4> >::type> f = boost::bind(foo, _1, _2, _3, _4);
#endif
    /* wrap with ref()/cref() to correct this */
    f(1, 2, 3, boost::cref(c));
    /* or...... */
    f.operator()<int,int,int,const C&>(1, 2, 3, c);
}

void test_function_obj() {
    c0 _c0;
    c1 _c1;
    c2 _c2;
    /* specify return type explicitly */
    boost::function<void()> f0 = boost::bind<void>(_c0);
    f0();
    /* do not specify return type */
    boost::function<void(int)> f10 = boost::bind(_c1, _1);
    f10(1);
    boost::function<void()> f11 = boost::bind(_c1, 1);
    f11();
    /* boost::ref() */
    boost::function<void(int,int)> f2 = boost::bind(boost::ref(_c2), _1, _2);
    f2(1, 2);
}

int main()
{
    test_internal();
    test_bug();
    test_bug2();
    test_function_obj();
    return 0;
}
