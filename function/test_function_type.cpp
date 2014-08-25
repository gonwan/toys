#include <iostream>
#include <typeinfo>
using namespace std;


template <typename Sig>
struct function;

template <typename R>
struct function<R ()> {
    void print() {
        cout << "R=" << typeid(R).name() << endl;
    }
};

template <typename R, typename T1>
struct function<R (T1)> {
    void print() {
        cout << "R=" << typeid(R).name() << endl;
        cout << "T1=" << typeid(T1).name() << endl;
    }
};

template <typename R, typename T1, typename T2>
struct function<R (T1, T2)> {
    void print() {
        cout << "R=" << typeid(R).name() << endl;
        cout << "T1=" << typeid(T1).name() << endl;
        cout << "T2=" << typeid(T2).name() << endl;
    }
};

int main()
{
    function<void ()> p0;
    p0.print();
    function<int (int)> p1;
    p1.print();
    function<char (char, char)> p2;
    p2.print();
    return 0;
}
