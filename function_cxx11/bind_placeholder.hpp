#ifndef _GL_BIND_PLACEHOLDER_HPP
#define _GL_BIND_PLACEHOLDER_HPP


namespace gl {


template <typename T>
struct is_placeholder {
    const static int value = 0;
};


template <int I>
struct arg {
    arg() { }
    arg(const arg<I> &) { }
};


template <int I>
struct is_placeholder<arg<I> > {
    const static int value = I;
};

template <int I>
struct is_placeholder<arg<I>(*)()> {
    const static int value = I;
};


} /* gl */


/* global namespace */
static gl::arg<1> _1;
static gl::arg<2> _2;
static gl::arg<3> _3;
static gl::arg<4> _4;
static gl::arg<5> _5;
static gl::arg<6> _6;
static gl::arg<7> _7;
static gl::arg<8> _8;
static gl::arg<9> _9;


#endif
