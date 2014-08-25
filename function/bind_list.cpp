#include "bind_list.hpp"
#include <iostream>
//#include <boost/bind.hpp>
using namespace gl;


void foo(char a, int b, double c) {
    std::cout << a << "," << b << "," << c << std::endl;
}

void test_boost_bind() {
    /*
    boost::_bi::list1<double > args(1.23);
    boost::_bi::list2<boost::_bi::value<int>, boost::arg<1> > func(boost::_bi::value<int>(1), _1);
    func(boost::_bi::type<void>(), foo, args, 0);
    */
}

int main()
{
    test_boost_bind();

    detail::list2<int, double> l2(1, 2.3);
    detail::list3<detail::value<char>, arg<1>, arg<2> > l32(detail::value<char>('a'), _1, _2);
    l32(detail::type<void>(), foo, l2);

    //std::cout << is_placeholder<arg<1> >::value << std::endl;
    return 0;
}
