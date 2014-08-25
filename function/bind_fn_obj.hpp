#ifndef _GL_BIND_FN_OBJ_HPP
#define _GL_BIND_FN_OBJ_HPP


#include "bind_list.hpp"


/*
 * gl::bind() for function objects _and_ function object references.
 */
namespace gl {


/* generic function objects, need to manually specify return type. */
template <typename R, typename F>
    detail::bind_t<R, F, detail::list0>
    bind(F f)
{
    typedef detail::list0 list_type;
    return detail::bind_t<R, F, list_type>(f, list_type());
}

template <typename R, typename F, typename A1>
    detail::bind_t<R, F, typename detail::list_helper1<A1>::type>
    bind(F f, A1 a1)
{
    typedef typename detail::list_helper1<A1>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(a1));
}

template <typename R, typename F, typename A1, typename A2>
    detail::bind_t<R, F, typename detail::list_helper2<A1, A2>::type>
    bind(F f, A1 a1, A2 a2)
{
    typedef typename detail::list_helper2<A1, A2>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(a1, a2));
}

template <typename R, typename F, typename A1, typename A2, typename A3>
    detail::bind_t<R, F, typename detail::list_helper3<A1, A2, A3>::type>
    bind(F f, A1 a1, A2 a2, A3 a3)
{
    typedef typename detail::list_helper3<A1, A2, A3>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(a1, a2, a3));
}

template <typename R, typename F, typename A1, typename A2, typename A3, typename A4>
    detail::bind_t<R, F, typename detail::list_helper4<A1, A2, A3, A4>::type>
    bind(F f, A1 a1, A2 a2, A3 a3, A4 a4)
{
    typedef typename detail::list_helper4<A1, A2, A3, A4>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(a1, a2, a3, a4));
}

template <typename R, typename F, typename A1, typename A2, typename A3, typename A4, typename A5>
    detail::bind_t<R, F, typename detail::list_helper5<A1, A2, A3, A4, A5>::type>
    bind(F f, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5)
{
    typedef typename detail::list_helper5<A1, A2, A3, A4, A5>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(a1, a2, a3, a4, a5));
}

template <typename R, typename F, typename A1, typename A2, typename A3, typename A4, typename A5, typename A6>
    detail::bind_t<R, F, typename detail::list_helper6<A1, A2, A3, A4, A5, A6>::type>
    bind(F f, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6)
{
    typedef typename detail::list_helper6<A1, A2, A3, A4, A5, A6>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(a1, a2, a3, a4, a5, a6));
}

template <typename R, typename F, typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7>
    detail::bind_t<R, F, typename detail::list_helper7<A1, A2, A3, A4, A5, A6, A7>::type>
    bind(F f, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7)
{
    typedef typename detail::list_helper7<A1, A2, A3, A4, A5, A6, A7>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(a1, a2, a3, a4, a5, a6, a7));
}

template <typename R, typename F, typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8>
    detail::bind_t<R, F, typename detail::list_helper8<A1, A2, A3, A4, A5, A6, A7, A8>::type>
    bind(F f, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8)
{
    typedef typename detail::list_helper8<A1, A2, A3, A4, A5, A6, A7, A8>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(a1, a2, a3, a4, a5, a6, a7, a8));
}

template <typename R, typename F, typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8, typename A9>
    detail::bind_t<R, F, typename detail::list_helper9<A1, A2, A3, A4, A5, A6, A7, A8, A9>::type>
    bind(F f, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8, A9 a9)
{
    typedef typename detail::list_helper9<A1, A2, A3, A4, A5, A6, A7, A8, A9>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(a1, a2, a3, a4, a5, a6, a7, a8, a9));
}


/* If no return type is specified, we will try to retrieve it from F::result_type. */
template <typename F>
    detail::bind_t<detail::unspecified, F, detail::list0>
    bind(F f)
{
    typedef detail::list0 list_type;
    return detail::bind_t<detail::unspecified, F, list_type>(f, list_type());
}

template <typename F, typename A1>
    detail::bind_t<detail::unspecified, F, typename detail::list_helper1<A1>::type>
    bind(F f, A1 a1)
{
    typedef typename detail::list_helper1<A1>::type list_type;
    return detail::bind_t<detail::unspecified, F, list_type>(f, list_type(a1));
}

template <typename F, typename A1, typename A2>
    detail::bind_t<detail::unspecified, F, typename detail::list_helper2<A1, A2>::type>
    bind(F f, A1 a1, A2 a2)
{
    typedef typename detail::list_helper2<A1, A2>::type list_type;
    return detail::bind_t<detail::unspecified, F, list_type>(f, list_type(a1, a2));
}

template <typename F, typename A1, typename A2, typename A3>
    detail::bind_t<detail::unspecified, F, typename detail::list_helper3<A1, A2, A3>::type>
    bind(F f, A1 a1, A2 a2, A3 a3)
{
    typedef typename detail::list_helper3<A1, A2, A3>::type list_type;
    return detail::bind_t<detail::unspecified, F, list_type>(f, list_type(a1, a2, a3));
}

template <typename F, typename A1, typename A2, typename A3, typename A4>
    detail::bind_t<detail::unspecified, F, typename detail::list_helper4<A1, A2, A3, A4>::type>
    bind(F f, A1 a1, A2 a2, A3 a3, A4 a4)
{
    typedef typename detail::list_helper4<A1, A2, A3, A4>::type list_type;
    return detail::bind_t<detail::unspecified, F, list_type>(f, list_type(a1, a2, a3, a4));
}

template <typename F, typename A1, typename A2, typename A3, typename A4, typename A5>
    detail::bind_t<detail::unspecified, F, typename detail::list_helper5<A1, A2, A3, A4, A5>::type>
    bind(F f, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5)
{
    typedef typename detail::list_helper5<A1, A2, A3, A4, A5>::type list_type;
    return detail::bind_t<detail::unspecified, F, list_type>(f, list_type(a1, a2, a3, a4, a5));
}

template <typename F, typename A1, typename A2, typename A3, typename A4, typename A5, typename A6>
    detail::bind_t<detail::unspecified, F, typename detail::list_helper6<A1, A2, A3, A4, A5, A6>::type>
    bind(F f, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6)
{
    typedef typename detail::list_helper6<A1, A2, A3, A4, A5, A6>::type list_type;
    return detail::bind_t<detail::unspecified, F, list_type>(f, list_type(a1, a2, a3, a4, a5, a6));
}

template <typename F, typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7>
    detail::bind_t<detail::unspecified, F, typename detail::list_helper7<A1, A2, A3, A4, A5, A6, A7>::type>
    bind(F f, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7)
{
    typedef typename detail::list_helper7<A1, A2, A3, A4, A5, A6, A7>::type list_type;
    return detail::bind_t<detail::unspecified, F, list_type>(f, list_type(a1, a2, a3, a4, a5, a6, a7));
}

template <typename F, typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8>
    detail::bind_t<detail::unspecified, F, typename detail::list_helper8<A1, A2, A3, A4, A5, A6, A7, A8>::type>
    bind(F f, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8)
{
    typedef typename detail::list_helper8<A1, A2, A3, A4, A5, A6, A7, A8>::type list_type;
    return detail::bind_t<detail::unspecified, F, list_type>(f, list_type(a1, a2, a3, a4, a5, a6, a7, a8));
}

template <typename F, typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8, typename A9>
    detail::bind_t<detail::unspecified, F, typename detail::list_helper9<A1, A2, A3, A4, A5, A6, A7, A8, A9>::type>
    bind(F f, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8, A9 a9)
{
    typedef typename detail::list_helper9<A1, A2, A3, A4, A5, A6, A7, A8, A9>::type list_type;
    return detail::bind_t<detail::unspecified, F, list_type>(f, list_type(a1, a2, a3, a4, a5, a6, a7, a8, a9));
}


} /* gl */


#endif
