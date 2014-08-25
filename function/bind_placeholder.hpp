#ifndef _GL_BIND_PLACEHOLDER_HPP
#define _GL_BIND_PLACEHOLDER_HPP


#include "type_traits.hpp"
#include <iostream>


namespace gl {


template <class T>
struct is_placeholder {
    const static int value = 0;
};


template <int I>
struct arg {
    arg() { }
    template <class T> arg(T const & /* t */ ) {
        //std::cout << "i=" << I << ",value=" << is_placeholder<T>::value << std::endl;
        typedef char T_must_be_placeholder[(I == is_placeholder<T>::value) ? 1: -1];
    }
    //arg(const arg<I> &) { }
};


template <int I>
struct is_placeholder<arg<I> > {
    const static int value = I;
};

template <int I>
struct is_placeholder<arg<I>(*)()> {
    const static int value = I;
};


static arg<1> _1;
static arg<2> _2;
static arg<3> _3;
static arg<4> _4;
static arg<5> _5;
static arg<6> _6;
static arg<7> _7;
static arg<8> _8;
static arg<9> _9;


} /* gl */


#endif
