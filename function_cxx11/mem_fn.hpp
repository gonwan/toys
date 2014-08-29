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
#if 1
    /* NOTE: use a separate __TArgs to forward arguments */
    template <typename... __TArgs>
    R operator()(T *t, __TArgs&&... args) const {
        return (t->*f_)(std::forward<__TArgs>(args)...);
    }
#else
    R operator()(T *t, TArgs... args) const {
        return (t->*f_)(std::forward<TArgs>(args)...);
    }
#endif
};

template <typename R, typename T, typename... TArgs>
class cmf {
private:
    typedef R (T::*F)(TArgs... args) const;
    F f_;
public:
    typedef R result_type;
    explicit cmf(F f): f_(f) { } /* compile error here if type mismatch */
#if 1
    /* NOTE: use a separate __TArgs to forward arguments */
    template <typename... __TArgs>
    R operator()(const T *t, __TArgs&&... args) const {
        return (t->*f_)(std::forward<__TArgs>(args)...);
    }
#else
    R operator()(const T *t, TArgs... args) const {
        return (t->*f_)(std::forward<TArgs>(args)...);
    }
#endif
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
