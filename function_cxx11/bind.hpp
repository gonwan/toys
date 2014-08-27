#ifndef _GL_BIND_HPP
#define _GL_BIND_HPP


#include "bind_list.hpp"
#include <utility>


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


/*
 * The difference between gl::bind() and gl::function:
 * For gl::function, the parameter types of the invoker callback are specified explicitly in template arguments.
 * While for gl::bind(), the parameter types are not specified explicitly in most cases. They are deducted.
 */
template <typename R, typename F, typename L>
class bind_t {

public:

    typedef typename detail::result_traits<R, F>::type result_type;

    bind_t(F f, const L &l): f_(f), l_(l) { }

    /* eval */
    template <typename A>
    result_type eval(A &a) {
        return l_(type<result_type>(), f_, a);
    }
    template <typename A>
    result_type eval(A &a) const {
        return l_(type<result_type>(), f_, a);
    }

    template <typename... TArgs>
    result_type operator()(TArgs&&... args) {
        list<TArgs...> a(std::forward<TArgs>(args)...);
        return l_(type<result_type>(), f_, a);
    }
    template <typename... TArgs>
    result_type operator()(TArgs&&... args) const {
        list<TArgs...> a(std::forward<TArgs>(args)...);
        return l_(type<result_type>(), f_, a);
    }

private:

    F f_;
    L l_;

};


} /* detail */


/* with explicit return type. */
template <typename R, typename F, typename... TArgs>
typename detail::_bind_traits1<R, F, TArgs...>::bind_type
bind(F f, TArgs&&... args) {
    typedef typename detail::list_helper<TArgs...>::type list_type;
    return detail::bind_t<R, F, list_type>(f, list_type(std::forward<TArgs>(args)...));
}

/* without explicit return type */
template <typename F, typename... TArgs>
typename detail::_bind_traits2<F, TArgs...>::bind_type
bind(F f, TArgs&&... args) {
    typedef typename detail::function_traits<F>::result_type result_type;
    typedef typename detail::list_helper<TArgs...>::type list_type;
    return detail::bind_t<result_type, F, list_type>(f, list_type(std::forward<TArgs>(args)...));
}


} /* gl */


#endif
