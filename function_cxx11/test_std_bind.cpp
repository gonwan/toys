#include <functional>
using namespace std;
using namespace std::placeholders;

class C {
};

void foo(int, C&) {
}

void foo2(int, C&&) {
}

int main() {
    C c;
    function<void(int,C&)> f10 = bind(foo, _1, _2);
    f10(1, c);
    function<void(C&)> f11 = bind(foo, 1, _1);
    f11(c);
    function<void(int)> f12 = bind(foo, _1, c);
    f12(1);
    function<void()> f13 = bind(foo, 1, c);
    f13();
    function<void(int,C&&)> f20 = bind(foo2, _1, _2);
    f20(1, std::move(c));
    function<void(C&&)> f21 = bind(foo2, 1, _1);
    f21(std::move(c));
#if 0
    /*
     * Not comfirm to the standard. 
     * See: http://stackoverflow.com/questions/5126219/is-there-a-reference-wrapper-for-rvalue-references 
     */
    function<void(int)> f22 = bind(foo2, _1, std::move(c));
    f22(1);
    function<void()> f23 = bind(foo2, 1, std::move(c));
    f23();
#endif
    return 0;
}

