#ifndef _GL_BIND_HPP
#define _GL_BIND_HPP


#include "bind_list.hpp"
#include <utility>


namespace gl {


namespace detail {


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
        list<void(TArgs...)> a(std::forward<TArgs>(args)...);
        return l_(type<result_type>(), f_, a);
    }
    template <typename... TArgs>
    result_type operator()(TArgs&&... args) const {
        list<void(TArgs...)> a(std::forward<TArgs>(args)...);
        return l_(type<result_type>(), f_, a);
    }

private:

    F f_;
    L l_;

};


} /* detail */


} /* gl */


#include "bind_fn.hpp"
#include "bind_mem_fn.hpp"
#include "bind_fn_obj.hpp"


#endif
