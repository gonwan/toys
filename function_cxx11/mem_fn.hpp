#ifndef _GL_MEM_FN_HPP
#define _GL_MEM_FN_HPP


#include <utility>


namespace gl {


namespace detail {


template <typename R, typename T, typename... TArgs>
class mf {
private:
    typedef R (T::*F)(TArgs... args);
    F f_;
public:
    typedef R result_type;
    explicit mf(F f): f_(f) { } /* compile error here if type mismatch */
    /* forward arguments */
    template <typename U>
    R operator()(U *u, TArgs&&... args) const {
        return (u->*f_)(std::forward<TArgs>(args)...);
    }
    template <typename U>
    R operator()(U &u, TArgs&&... args) const {
        return (u.*f_)(std::forward<TArgs>(args)...);
    }
};

template <typename R, typename T, typename... TArgs>
class cmf {
private:
    typedef R (T::*F)(TArgs... args) const;
    F f_;
public:
    typedef R result_type;
    explicit cmf(F f): f_(f) { } /* compile error here if type mismatch */
    /* forward arguments */
    template <typename U>
    R operator()(U *u, TArgs&&... args) const {
        return (u->*f_)(std::forward<TArgs>(args)...);
    }
    template <typename U>
    R operator()(U &u, TArgs&&... args) const {
        return (u.*f_)(std::forward<TArgs>(args)...);
    }
};


} /* detail */


template <typename R, typename T, typename... TArgs>
detail::mf<R, T, TArgs...> mem_fn(R (T::*f)(TArgs...)) {
    return detail::mf<R, T, TArgs...>(f);
}

template <typename R, typename T, typename... TArgs>
detail::cmf<R, T, TArgs...> mem_fn(R (T::*f)(TArgs...) const) {
    return detail::cmf<R, T, TArgs...>(f);
}


} /* gl */


#endif
