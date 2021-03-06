#ifndef _GL_BIND_MEM_FN_HPP
#define _GL_BIND_MEM_FN_HPP


#include "bind_list.hpp"
#include "mem_fn.hpp"


namespace gl {


/*
 * Boost has return type overloads, but we do not define them.
 * Since gl::bind() is supposed to be used with gl::function, and gl::function has this ability.
 */


/* 0 */
template <typename R, typename T,
    typename A1>
    detail::bind_t<R, detail::mf0<R, T>, typename detail::list_helper1<A1>::type>
    bind(R(T::*f)(), A1 a1)
{
    typedef detail::mf0<R, T> F;
    typedef typename detail::list_helper1<A1>::type list_type;
    return detail::bind_t<R, F, list_type>(F(f), list_type(a1));
}

template <typename R, typename T,
    typename A1>
    detail::bind_t<R, detail::cmf0<R, T>, typename detail::list_helper1<A1>::type>
    bind(R(T::*f)()const, A1 a1)
{
    typedef detail::cmf0<R, T> F;
    typedef typename detail::list_helper1<A1>::type list_type;
    return detail::bind_t<R, F, list_type>(F(f), list_type(a1));
}

/* 1 */
template <typename R, typename T,
    typename B1,
    typename A1, typename A2>
    detail::bind_t<R, detail::mf1<R, T, B1>, typename detail::list_helper2<A1, A2>::type>
    bind(R(T::*f)(B1), A1 a1, A2 a2)
{
    typedef detail::mf1<R, T, B1> F;
    typedef typename detail::list_helper2<A1, A2>::type list_type;
    return detail::bind_t<R, F, list_type>(F(f), list_type(a1, a2));
}

template <typename R, typename T,
    typename B1,
    typename A1, typename A2>
    detail::bind_t<R, detail::cmf1<R, T, B1>, typename detail::list_helper2<A1, A2>::type>
    bind(R(T::*f)(B1)const, A1 a1, A2 a2)
{
    typedef detail::cmf1<R, T, B1> F;
    typedef typename detail::list_helper2<A1, A2>::type list_type;
    return detail::bind_t<R, F, list_type>(F(f), list_type(a1, a2));
}

/* 2 */
template <typename R, typename T,
    typename B1, typename B2,
    typename A1, typename A2, typename A3>
    detail::bind_t<R, detail::mf2<R, T, B1, B2>, typename detail::list_helper3<A1, A2, A3>::type>
    bind(R(T::*f)(B1,B2), A1 a1, A2 a2, A3 a3)
{
    typedef detail::mf2<R, T, B1, B2> F;
    typedef typename detail::list_helper3<A1, A2, A3>::type list_type;
    return detail::bind_t<R, F, list_type>(F(f), list_type(a1, a2, a3));
}

template <typename R, typename T,
    typename B1, typename B2,
    typename A1, typename A2, typename A3>
    detail::bind_t<R, detail::cmf2<R, T, B1, B2>, typename detail::list_helper3<A1, A2, A3>::type>
    bind(R(T::*f)(B1,B2)const, A1 a1, A2 a2, A3 a3)
{
    typedef detail::cmf2<R, T, B1, B2> F;
    typedef typename detail::list_helper3<A1, A2, A3>::type list_type;
    return detail::bind_t<R, F, list_type>(F(f), list_type(a1, a2, a3));
}

/* 3 */
template <typename R, typename T,
    typename B1, typename B2, typename B3,
    typename A1, typename A2, typename A3, typename A4>
    detail::bind_t<R, detail::mf3<R, T, B1, B2, B3>, typename detail::list_helper4<A1, A2, A3, A4>::type>
    bind(R(T::*f)(B1,B2,B3), A1 a1, A2 a2, A3 a3, A4 a4)
{
    typedef detail::mf3<R, T, B1, B2, B3> F;
    typedef typename detail::list_helper4<A1, A2, A3, A4>::type list_type;
    return detail::bind_t<R, F, list_type>(F(f), list_type(a1, a2, a3, a4));
}

template <typename R, typename T,
    typename B1, typename B2, typename B3,
    typename A1, typename A2, typename A3, typename A4>
    detail::bind_t<R, detail::cmf3<R, T, B1, B2, B3>, typename detail::list_helper4<A1, A2, A3, A4>::type>
    bind(R(T::*f)(B1,B2,B3)const, A1 a1, A2 a2, A3 a3, A4 a4)
{
    typedef detail::cmf3<R, T, B1, B2, B3> F;
    typedef typename detail::list_helper4<A1, A2, A3, A4>::type list_type;
    return detail::bind_t<R, F, list_type>(F(f), list_type(a1, a2, a3, a4));
}

/* 4 */
template <typename R, typename T,
    typename B1, typename B2, typename B3, typename B4,
    typename A1, typename A2, typename A3, typename A4, typename A5>
    detail::bind_t<R, detail::mf4<R, T, B1, B2, B3, B4>, typename detail::list_helper5<A1, A2, A3, A4, A5>::type>
    bind(R(T::*f)(B1,B2,B3,B4), A1 a1, A2 a2, A3 a3, A4 a4, A5 a5)
{
    typedef detail::mf4<R, T, B1, B2, B3, B4> F;
    typedef typename detail::list_helper5<A1, A2, A3, A4, A5>::type list_type;
    return detail::bind_t<R, F, list_type>(F(f), list_type(a1, a2, a3, a4, a5));
}

template <typename R, typename T,
    typename B1, typename B2, typename B3, typename B4,
    typename A1, typename A2, typename A3, typename A4, typename A5>
    detail::bind_t<R, detail::cmf4<R, T, B1, B2, B3, B4>, typename detail::list_helper5<A1, A2, A3, A4, A5>::type>
    bind(R(T::*f)(B1,B2,B3,B4)const, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5)
{
    typedef detail::cmf4<R, T, B1, B2, B3, B4> F;
    typedef typename detail::list_helper5<A1, A2, A3, A4, A5>::type list_type;
    return detail::bind_t<R, F, list_type>(F(f), list_type(a1, a2, a3, a4, a5));
}

/* 5 */
template <typename R, typename T,
    typename B1, typename B2, typename B3, typename B4, typename B5,
    typename A1, typename A2, typename A3, typename A4, typename A5, typename A6>
    detail::bind_t<R, detail::mf5<R, T, B1, B2, B3, B4, B5>, typename detail::list_helper6<A1, A2, A3, A4, A5, A6>::type>
    bind(R(T::*f)(B1,B2,B3,B4,B5), A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6)
{
    typedef detail::mf5<R, T, B1, B2, B3, B4, B5> F;
    typedef typename detail::list_helper6<A1, A2, A3, A4, A5, A6>::type list_type;
    return detail::bind_t<R, F, list_type>(F(f), list_type(a1, a2, a3, a4, a5, a6));
}

template <typename R, typename T,
    typename B1, typename B2, typename B3, typename B4, typename B5,
    typename A1, typename A2, typename A3, typename A4, typename A5, typename A6>
    detail::bind_t<R, detail::cmf5<R, T, B1, B2, B3, B4, B5>, typename detail::list_helper6<A1, A2, A3, A4, A5, A6>::type>
    bind(R(T::*f)(B1,B2,B3,B4,B5)const, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6)
{
    typedef detail::cmf5<R, T, B1, B2, B3, B4, B5> F;
    typedef typename detail::list_helper6<A1, A2, A3, A4, A5, A6>::type list_type;
    return detail::bind_t<R, F, list_type>(F(f), list_type(a1, a2, a3, a4, a5, a6));
}

/* 6 */
template <typename R, typename T,
    typename B1, typename B2, typename B3, typename B4, typename B5, typename B6,
    typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7>
    detail::bind_t<R, detail::mf6<R, T, B1, B2, B3, B4, B5, B6>, typename detail::list_helper7<A1, A2, A3, A4, A5, A6, A7>::type>
    bind(R(T::*f)(B1,B2,B3,B4,B5,B6), A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7)
{
    typedef detail::mf6<R, T, B1, B2, B3, B4, B5, B6> F;
    typedef typename detail::list_helper7<A1, A2, A3, A4, A5, A6, A7>::type list_type;
    return detail::bind_t<R, F, list_type>(F(f), list_type(a1, a2, a3, a4, a5, a6, a7));
}

template <typename R, typename T,
    typename B1, typename B2, typename B3, typename B4, typename B5, typename B6,
    typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7>
    detail::bind_t<R, detail::cmf6<R, T, B1, B2, B3, B4, B5, B6>, typename detail::list_helper7<A1, A2, A3, A4, A5, A6, A7>::type>
    bind(R(T::*f)(B1,B2,B3,B4,B5,B6)const, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7)
{
    typedef detail::cmf6<R, T, B1, B2, B3, B4, B5, B6> F;
    typedef typename detail::list_helper7<A1, A2, A3, A4, A5, A6, A7>::type list_type;
    return detail::bind_t<R, F, list_type>(F(f), list_type(a1, a2, a3, a4, a5, a6, a7));
}

/* 7 */
template <typename R, typename T,
    typename B1, typename B2, typename B3, typename B4, typename B5, typename B6, typename B7,
    typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8>
    detail::bind_t<R, detail::mf7<R, T, B1, B2, B3, B4, B5, B6, B7>, typename detail::list_helper8<A1, A2, A3, A4, A5, A6, A7, A8>::type>
    bind(R(T::*f)(B1,B2,B3,B4,B5,B6,B7), A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8)
{
    typedef detail::mf7<R, T, B1, B2, B3, B4, B5, B6, B7> F;
    typedef typename detail::list_helper8<A1, A2, A3, A4, A5, A6, A7, A8>::type list_type;
    return detail::bind_t<R, F, list_type>(F(f), list_type(a1, a2, a3, a4, a5, a6, a7, a8));
}

template <typename R, typename T,
    typename B1, typename B2, typename B3, typename B4, typename B5, typename B6, typename B7,
    typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8>
    detail::bind_t<R, detail::cmf7<R, T, B1, B2, B3, B4, B5, B6, B7>, typename detail::list_helper8<A1, A2, A3, A4, A5, A6, A7, A8>::type>
    bind(R(T::*f)(B1,B2,B3,B4,B5,B6,B7)const, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8)
{
    typedef detail::cmf7<R, T, B1, B2, B3, B4, B5, B6, B7> F;
    typedef typename detail::list_helper8<A1, A2, A3, A4, A5, A6, A7, A8>::type list_type;
    return detail::bind_t<R, F, list_type>(F(f), list_type(a1, a2, a3, a4, a5, a6, a7, a8));
}

/* 8 */
template <typename R, typename T,
    typename B1, typename B2, typename B3, typename B4, typename B5, typename B6, typename B7, typename B8,
    typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8, typename A9>
    detail::bind_t<R, detail::mf8<R, T, B1, B2, B3, B4, B5, B6, B7, B8>, typename detail::list_helper9<A1, A2, A3, A4, A5, A6, A7, A8, A9>::type>
    bind(R(T::*f)(B1,B2,B3,B4,B5,B6,B7,B8), A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8, A9 a9)
{
    typedef detail::mf8<R, T, B1, B2, B3, B4, B5, B6, B7, B8> F;
    typedef typename detail::list_helper9<A1, A2, A3, A4, A5, A6, A7, A8, A9>::type list_type;
    return detail::bind_t<R, F, list_type>(F(f), list_type(a1, a2, a3, a4, a5, a6, a7, a8, a9));
}

template <typename R, typename T,
    typename B1, typename B2, typename B3, typename B4, typename B5, typename B6, typename B7, typename B8,
    typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8, typename A9>
    detail::bind_t<R, detail::cmf8<R, T, B1, B2, B3, B4, B5, B6, B7, B8>, typename detail::list_helper9<A1, A2, A3, A4, A5, A6, A7, A8, A9>::type>
    bind(R(T::*f)(B1,B2,B3,B4,B5,B6,B7,B8)const, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8, A9 a9)
{
    typedef detail::cmf8<R, T, B1, B2, B3, B4, B5, B6, B7, B8> F;
    typedef typename detail::list_helper9<A1, A2, A3, A4, A5, A6, A7, A8, A9>::type list_type;
    return detail::bind_t<R, F, list_type>(F(f), list_type(a1, a2, a3, a4, a5, a6, a7, a8, a9));
}


} /* gl */


#endif

