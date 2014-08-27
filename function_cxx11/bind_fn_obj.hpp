#ifndef _GL_BIND_FN_OBJ_HPP
#define _GL_BIND_FN_OBJ_HPP


#include "bind_list.hpp"
#include <utility>


/*
 * gl::bind() for function objects _and_ function object references.
 */
namespace gl {


/* generic function objects, need to manually specify return type. */
template <typename R, typename F, typename... TArgs>
    detail::bind_t<R, F, typename detail::list_helper<TArgs...>::type>
    bind(F f, TArgs&&... args)
{
    typedef typename detail::list_helper<TArgs...>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(std::forward<TArgs>(args)...));
}

/* If no return type is specified, we will try to retrieve it from F::result_type. */
template <typename F, typename... TArgs>
    detail::bind_t<detail::unspecified, F, typename detail::list_helper<TArgs...>::type>
    bind(F f, TArgs&&... args)
{
    typedef typename detail::list_helper<TArgs...>::type list_type;
    return detail::bind_t<detail::unspecified, F, list_type>(f, list_type(std::forward<TArgs>(args)...));
}


} /* gl */


#endif
