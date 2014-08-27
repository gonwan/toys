#ifndef _GL_BIND_FN_HPP
#define _GL_BIND_FN_HPP


#include "bind_list.hpp"


namespace gl {


/*
template <typename R, typename... TBs, typename... TArgs>
    detail::bind_t<R, R(*)(TBs...), typename detail::list_helper<void(TArgs...)>::type>
    bind(R(*f)(TBs...), TArgs&&... args)
{
    typedef R (*F)(TBs...);
    typedef typename detail::list_helper<void(TArgs...)>::type list_type;
    return detail::bind_t<R, F, list_type>(F(f), list_type(std::forward<TArgs>(args)...));
}
*/

#if 1

template <typename R>
    detail::bind_t<R, R(*)(), detail::list0>
    bind(R(*f)())
{
    typedef R (*F)();
    typedef detail::list0 list_type;
    return detail::bind_t<R, F, list_type>(f, list_type());
}

template <typename R, typename B1, typename A1>
    detail::bind_t<R, R(*)(B1), typename detail::list_helper1<A1>::type>
    bind(R(*f)(B1), A1 a1)
{
    typedef R (*F)(B1);
    typedef typename detail::list_helper1<A1>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(a1));
}

template <typename R, typename B1, typename B2, typename A1, typename A2>
    detail::bind_t<R, R(*)(B1,B2), typename detail::list_helper2<A1, A2>::type>
    bind(R(*f)(B1,B2), A1 a1, A2 a2)
{
    typedef R (*F)(B1, B2);
    typedef typename detail::list_helper2<A1, A2>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(a1, a2));
}

template <typename R,
    typename B1, typename B2, typename B3,
    typename A1, typename A2, typename A3>
    detail::bind_t<R, R(*)(B1,B2,B3), typename detail::list_helper3<A1, A2, A3>::type>
    bind(R(*f)(B1,B2,B3), A1 a1, A2 a2, A3 a3)
{
    typedef R (*F)(B1, B2, B3);
    typedef typename detail::list_helper3<A1, A2, A3>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(a1, a2, a3));
}

template <typename R,
    typename B1, typename B2, typename B3, typename B4,
    typename A1, typename A2, typename A3, typename A4>
    detail::bind_t<R, R(*)(B1,B2,B3,B4), typename detail::list_helper4<A1, A2, A3, A4>::type>
    bind(R(*f)(B1,B2,B3,B4), A1 a1, A2 a2, A3 a3, A4 a4)
{
    typedef R (*F)(B1, B2, B3, B4);
    typedef typename detail::list_helper4<A1, A2, A3, A4>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(a1, a2, a3, a4));
}

template <typename R,
    typename B1, typename B2, typename B3, typename B4, typename B5,
    typename A1, typename A2, typename A3, typename A4, typename A5>
    detail::bind_t<R, R(*)(B1,B2,B3,B4,B5), typename detail::list_helper5<A1, A2, A3, A4, A5>::type>
    bind(R(*f)(B1,B2,B3,B4,B5), A1 a1, A2 a2, A3 a3, A4 a4, A5 a5)
{
    typedef R (*F)(B1, B2, B3, B4, B5);
    typedef typename detail::list_helper5<A1, A2, A3, A4, A5>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(a1, a2, a3, a4, a5));
}

template <typename R,
    typename B1, typename B2, typename B3, typename B4, typename B5, typename B6,
    typename A1, typename A2, typename A3, typename A4, typename A5, typename A6>
    detail::bind_t<R, R(*)(B1,B2,B3,B4,B5,B6), typename detail::list_helper6<A1, A2, A3, A4, A5, A6>::type>
    bind(R(*f)(B1,B2,B3,B4,B5,B6), A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6)
{
    typedef R (*F)(B1, B2, B3, B4, B5, B6);
    typedef typename detail::list_helper6<A1, A2, A3, A4, A5, A6>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(a1, a2, a3, a4, a5, a6));
}

template <typename R,
    typename B1, typename B2, typename B3, typename B4, typename B5, typename B6, typename B7,
    typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7>
    detail::bind_t<R, R(*)(B1, B2, B3, B4, B5, B6, B7), typename detail::list_helper7<A1, A2, A3, A4, A5, A6, A7>::type>
    bind(R(*f)(B1, B2, B3, B4, B5, B6, B7), A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7)
{
    typedef R (*F)(B1, B2, B3, B4, B5, B6, B7);
    typedef typename detail::list_helper7<A1, A2, A3, A4, A5, A6, A7>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(a1, a2, a3, a4, a5, a6, a7));
}

template <typename R,
    typename B1, typename B2, typename B3, typename B4, typename B5, typename B6, typename B7, typename B8,
    typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8>
    detail::bind_t<R, R(*)(B1,B2,B3,B4,B5,B6,B7,B8), typename detail::list_helper8<A1, A2, A3, A4, A5, A6, A7, A8>::type>
    bind(R(*f)(B1,B2,B3,B4,B5,B6,B7,B8), A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8)
{
    typedef R (*F)(B1, B2, B3, B4, B5, B6, B7, B8);
    typedef typename detail::list_helper8<A1, A2, A3, A4, A5, A6, A7, A8>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(a1, a2, a3, a4, a5, a6, a7, a8));
}

template <typename R,
    typename B1, typename B2, typename B3, typename B4, typename B5, typename B6, typename B7, typename B8, typename B9,
    typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8, typename A9>
    detail::bind_t<R, R(*)(B1,B2,B3,B4,B5,B6,B7,B8,B9), typename detail::list_helper9<A1, A2, A3, A4, A5, A6, A7, A8, A9>::type>
    bind(R(*f)(B1,B2,B3,B4,B5,B6,B7,B8,B9), A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8, A9 a9)
{
    typedef R (*F)(B1, B2, B3, B4, B5, B6, B7, B8, B9);
    typedef typename detail::list_helper9<A1, A2, A3, A4, A5, A6, A7, A8, A9>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(a1, a2, a3, a4, a5, a6, a7, a8, a9));
}

#endif

} /* gl */


#endif
