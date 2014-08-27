#ifndef _GL_BIND_FN_OBJ_HPP
#define _GL_BIND_FN_OBJ_HPP


#include "bind_list.hpp"
#include <utility>


/*
 * gl::bind() for function objects _and_ function object references.
 */
namespace gl {


namespace detail {


/* to return type of a function */
template <typename F>
struct function_traits {
    typedef unspecified result_type;
};

/*template <typename R, typename... TArgs>
class result_traits<R(TArgs...)> {
    typedef R result_type;
};*/

template <typename R, typename... TArgs>
struct function_traits<R(*)(TArgs...)> {
    typedef R result_type;
};

template <typename R, typename T, typename... TArgs>
struct function_traits<R(T::*)(TArgs...)> {
    typedef R result_type;
};

template <typename R, typename T, typename... TArgs>
struct function_traits<R(T::*)(TArgs...)const> {
    typedef R result_type;
};

template <typename R, typename F>
struct function_traits2 {
    typedef R result_type;
};

template <typename F>
struct function_traits2<unspecified, F> {
    typedef typename function_traits<F>::result_type result_type;
};

/* to get return type of bind() */
template <typename R, typename F, typename... TArgs>
struct _bind_traits1 {
    typedef typename detail::list_helper<TArgs...>::type list_type;
    typedef detail::bind_t<R, F, list_type> bind_type;
};

template <typename F, typename... TArgs>
struct _bind_traits2 {
    typedef typename detail::list_helper<TArgs...>::type list_type;
    typedef typename detail::function_traits<F>::result_type result_type;
    typedef detail::bind_t<result_type, F, list_type> bind_type;
};


} /* detail */


/* generic function objects, need to manually specify return type. */
template <typename R, typename F, typename... TArgs>
typename detail::_bind_traits1<R, F, TArgs...>::bind_type
bind(F f, TArgs&&... args)
{
    typedef typename detail::list_helper<TArgs...>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(std::forward<TArgs>(args)...));
}

/* If no return type is specified, we will try to retrieve it from F::result_type. */
template <typename F, typename... TArgs>
typename detail::_bind_traits2<F, TArgs...>::bind_type
bind(F f, TArgs&&... args) {
    typedef typename detail::function_traits<F>::result_type result_type;
    typedef typename detail::list_helper<TArgs...>::type list_type;
    return detail::bind_t<result_type, F, list_type>(f, list_type(std::forward<TArgs>(args)...));
}


} /* gl */


#endif
