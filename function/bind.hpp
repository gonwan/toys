#ifndef _GL_BIND_HPP
#define _GL_BIND_HPP


#include "bind_list.hpp"


namespace gl {


namespace detail {


/*
 * The difference between gl::bind() and gl::function:
 * For gl::function, the parameter types of the invoker callback are specified explicitly in template arguments.
 * While for gl::bind(), the parameter types are not specified explicitly in most cases. They are deducted.
 */
template <class R, class F, class L>
class bind_t {

public:

    typedef typename detail::result_trait<R, F>::type result_type;

    bind_t(F f, L const & l): f_(f), l_(l) { }

    /*
     * A large number of operator() overload is removed as compared to boost,
     * since they do not enumerate all possible combination of arguments.
     * Use gl::ref() for reference and gl::cref() for const reference explicitly.
     */
    result_type operator()() const {
        list0 a;
        return l_(type<result_type>(), f_, a, 0);
    }

    template <typename A1>
    result_type operator()(A1 a1) const {
        list1<A1> a(a1);
        return l_(type<result_type>(), f_, a, 0);
    }

    template <typename A1, typename A2>
    result_type operator()(A1 a1, A2 a2) const {
        list2<A1, A2> a(a1, a2);
        return l_(type<result_type>(), f_, a, 0);
    }

    template <typename A1, typename A2, typename A3>
    result_type operator()(A1 a1, A2 a2, A3 a3) const {
        list3<A1, A2, A3> a(a1, a2, a3);
        return l_(type<result_type>(), f_, a, 0);
    }

    template <typename A1, typename A2, typename A3, typename A4>
    result_type operator()(A1 a1, A2 a2, A3 a3, A4 a4) const {
        list4<A1, A2, A3, A4> a(a1, a2, a3, a4);
        return l_(type<result_type>(), f_, a, 0);
    }

    template <typename A1, typename A2, typename A3, typename A4, typename A5>
    result_type operator()(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) const {
        list5<A1, A2, A3, A4, A5> a(a1, a2, a3, a4, a5);
        return l_(type<result_type>(), f_, a, 0);
    }

    template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6>
    result_type operator()(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6) const {
        list6<A1, A2, A3, A4, A5, A6> a(a1, a2, a3, a4, a5, a6);
        return l_(type<result_type>(), f_, a, 0);
    }

    template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7>
    result_type operator()(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7) const {
        list7<A1, A2, A3, A4, A5, A6, A7> a(a1, a2, a3, a4, a5, a6, a7);
        return l_(type<result_type>(), f_, a, 0);
    }

    template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8>
    result_type operator()(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8) const {
        list8<A1, A2, A3, A4, A5, A6, A7, A8> a(a1, a2, a3, a4, a5, a6, a7, a8);
        return l_(type<result_type>(), f_, a, 0);
    }

    template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8, typename A9>
    result_type operator()(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8, A9 a9) const {
        list9<A1, A2, A3, A4, A5, A6, A7, A8, A9> a(a1, a2, a3, a4, a5, a6, a7, a8, a9);
        return l_(type<result_type>(), f_, a, 0);
    }

    template <typename A>
    result_type eval(A &a) const {
        return l_(type<result_type>(), f_, a, 0);
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
