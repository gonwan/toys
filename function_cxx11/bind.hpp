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
template <typename R, typename F, typename L>
class bind_t {

public:

    typedef typename detail::result_traits<R, F>::type result_type;

    bind_t(F f, const L &l): f_(f), l_(l) { }

    /*
     * For maximum compatibility, a large number of operator() overloads are added, like what boost do.
     * But they still fail to enumerate all possible combinations of argument types.
     * Use gl::ref() for reference and gl::cref() to workaround.
     */

    /* eval */
    template <typename A>
    result_type eval(A &a) {
        return l_(type<result_type>(), f_, a);
    }
    template <typename A>
    result_type eval(A &a) const {
        return l_(type<result_type>(), f_, a);
    }
    /* 0 */
    result_type operator()() {
        list0 a;
        return l_(type<result_type>(), f_, a);
    }
    result_type operator()() const {
        list0 a;
        return l_(type<result_type>(), f_, a);
    }
    /* 1 */
    template <typename A1>
    result_type operator()(A1 &a1) {
        list1<A1&> a(a1);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1>
    result_type operator()(A1 &a1) const {
        list1<A1&> a(a1);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1>
    result_type operator()(const A1 &a1) {
        list1<const A1&> a(a1);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1>
    result_type operator()(const A1 &a1) const {
        list1<const A1&> a(a1);
        return l_(type<result_type>(), f_, a);
    }
    /* 2 */
    template <typename A1, typename A2>
    result_type operator()(A1 &a1, A2 &a2) {
        list2<A1&, A2&> a(a1, a2);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2>
    result_type operator()(A1 &a1, A2 &a2) const {
        list2<A1&, A2&> a(a1, a2);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2>
    result_type operator()(const A1 &a1, const A2 &a2) {
        list2<const A1&, const A2&> a(a1, a2);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2>
    result_type operator()(const A1 &a1, const A2 &a2) const {
        list2<const A1&, const A2&> a(a1, a2);
        return l_(type<result_type>(), f_, a);
    }
    /* 3 */
    template <typename A1, typename A2, typename A3>
    result_type operator()(A1 &a1, A2 &a2, A3 &a3) {
        list3<A1&, A2&, A3&> a(a1, a2, a3);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3>
    result_type operator()(A1 &a1, A2 &a2, A3 &a3) const {
        list3<A1&, A2&, A3&> a(a1, a2, a3);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3>
    result_type operator()(const A1 &a1, const A2 &a2, const A3 &a3) {
        list3<const A1&, const A2&, const A3&> a(a1, a2, a3);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3>
    result_type operator()(const A1 &a1, const A2 &a2, const A3 &a3) const {
        list3<const A1&, const A2&, const A3&> a(a1, a2, a3);
        return l_(type<result_type>(), f_, a);
    }
    /* 4 */
    template <typename A1, typename A2, typename A3, typename A4>
    result_type operator()(A1 &a1, A2 &a2, A3 &a3, A4 &a4) {
        list4<A1&, A2&, A3&, A4&> a(a1, a2, a3, a4);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3, typename A4>
    result_type operator()(A1 &a1, A2 &a2, A3 &a3, A4 &a4) const {
        list4<A1&, A2&, A3&, A4&> a(a1, a2, a3, a4);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3, typename A4>
    result_type operator()(const A1 &a1, const A2 &a2, const A3 &a3, const A4 &a4) {
        list4<const A1&, const A2&, const A3&, const A4&> a(a1, a2, a3, a4);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3, typename A4>
    result_type operator()(const A1 &a1, const A2 &a2, const A3 &a3, const A4 &a4) const {
        list4<const A1&, const A2&, const A3&, const A4&> a(a1, a2, a3, a4);
        return l_(type<result_type>(), f_, a);
    }
    /* 5 */
    template <typename A1, typename A2, typename A3, typename A4, typename A5>
    result_type operator()(A1 &a1, A2 &a2, A3 &a3, A4 &a4, A5 &a5) {
        list5<A1&, A2&, A3&, A4&, A5&> a(a1, a2, a3, a4, a5);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3, typename A4, typename A5>
    result_type operator()(A1 &a1, A2 &a2, A3 &a3, A4 &a4, A5 &a5) const {
        list5<A1&, A2&, A3&, A4&, A5&> a(a1, a2, a3, a4, a5);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3, typename A4, typename A5>
    result_type operator()(const A1 &a1, const A2 &a2, const A3 &a3, const A4 &a4, const A5 &a5) {
        list5<const A1&, const A2&, const A3&, const A4&, const A5&> a(a1, a2, a3, a4, a5);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3, typename A4, typename A5>
    result_type operator()(const A1 &a1, const A2 &a2, const A3 &a3, const A4 &a4, const A5 &a5) const {
        list5<const A1&, const A2&, const A3&, const A4&, const A5&> a(a1, a2, a3, a4, a5);
        return l_(type<result_type>(), f_, a);
    }
    /* 6 */
    template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6>
    result_type operator()(A1 &a1, A2 &a2, A3 &a3, A4 &a4, A5 &a5, A6 &a6) {
        list6<A1&, A2&, A3&, A4&, A5&, A6&> a(a1, a2, a3, a4, a5, a6);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6>
    result_type operator()(A1 &a1, A2 &a2, A3 &a3, A4 &a4, A5 &a5, A6 &a6) const {
        list6<A1&, A2&, A3&, A4&, A5&, A6&> a(a1, a2, a3, a4, a5, a6);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6>
    result_type operator()(const A1 &a1, const A2 &a2, const A3 &a3, const A4 &a4, const A5 &a5, const A6 &a6) {
        list6<const A1&, const A2&, const A3&, const A4&, const A5&, const A6&> a(a1, a2, a3, a4, a5, a6);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6>
    result_type operator()(const A1 &a1, const A2 &a2, const A3 &a3, const A4 &a4, const A5 &a5, const A6 &a6) const {
        list6<const A1&, const A2&, const A3&, const A4&, const A5&, const A6&> a(a1, a2, a3, a4, a5, a6);
        return l_(type<result_type>(), f_, a);
    }
    /* 7 */
    template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7>
    result_type operator()(A1 &a1, A2 &a2, A3 &a3, A4 &a4, A5 &a5, A6 &a6, A7 &a7) {
        list7<A1&, A2&, A3&, A4&, A5&, A6&, A7&> a(a1, a2, a3, a4, a5, a6, a7);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7>
    result_type operator()(A1 &a1, A2 &a2, A3 &a3, A4 &a4, A5 &a5, A6 &a6, A7 &a7) const {
        list7<A1&, A2&, A3&, A4&, A5&, A6&, A7&> a(a1, a2, a3, a4, a5, a6, a7);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7>
    result_type operator()(const A1 &a1, const A2 &a2, const A3 &a3, const A4 &a4, const A5 &a5, const A6 &a6, const A7 &a7) {
        list7<const A1&, const A2&, const A3&, const A4&, const A5&, const A6&, const A7&> a(a1, a2, a3, a4, a5, a6, a7);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7>
    result_type operator()(const A1 &a1, const A2 &a2, const A3 &a3, const A4 &a4, const A5 &a5, const A6 &a6, const A7 &a7) const {
        list7<const A1&, const A2&, const A3&, const A4&, const A5&, const A6&, const A7&> a(a1, a2, a3, a4, a5, a6, a7);
        return l_(type<result_type>(), f_, a);
    }
    /* 8 */
    template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8>
    result_type operator()(A1 &a1, A2 &a2, A3 &a3, A4 &a4, A5 &a5, A6 &a6, A7 &a7, A8 &a8) {
        list8<A1&, A2&, A3&, A4&, A5&, A6&, A7&, A8&> a(a1, a2, a3, a4, a5, a6, a7, a8);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8>
    result_type operator()(A1 &a1, A2 &a2, A3 &a3, A4 &a4, A5 &a5, A6 &a6, A7 &a7, A8 &a8) const {
        list8<A1&, A2&, A3&, A4&, A5&, A6&, A7&, A8&> a(a1, a2, a3, a4, a5, a6, a7, a8);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8>
    result_type operator()(const A1 &a1, const A2 &a2, const A3 &a3, const A4 &a4, const A5 &a5, const A6 &a6, const A7 &a7, const A8 &a8) {
        list8<const A1&, const A2&, const A3&, const A4&, const A5&, const A6&, const A7&, const A8&> a(a1, a2, a3, a4, a5, a6, a7, a8);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8>
    result_type operator()(const A1 &a1, const A2 &a2, const A3 &a3, const A4 &a4, const A5 &a5, const A6 &a6, const A7 &a7, const A8 &a8) const {
        list8<const A1&, const A2&, const A3&, const A4&, const A5&, const A6&, const A7&, const A8&> a(a1, a2, a3, a4, a5, a6, a7, a8);
        return l_(type<result_type>(), f_, a);
    }
    /* 9 */
    template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8, typename A9>
    result_type operator()(A1 &a1, A2 &a2, A3 &a3, A4 &a4, A5 &a5, A6 &a6, A7 &a7, A8 &a8, A9 &a9) {
        list9<A1&, A2&, A3&, A4&, A5&, A6&, A7&, A8&, A9&> a(a1, a2, a3, a4, a5, a6, a7, a8, a9);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8, typename A9>
    result_type operator()(A1 &a1, A2 &a2, A3 &a3, A4 &a4, A5 &a5, A6 &a6, A7 &a7, A8 &a8, A9 &a9) const {
        list9<A1&, A2&, A3&, A4&, A5&, A6&, A7&, A8&, A9&> a(a1, a2, a3, a4, a5, a6, a7, a8, a9);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8, typename A9>
    result_type operator()(const A1 &a1, const A2 &a2, const A3 &a3, const A4 &a4, const A5 &a5, const A6 &a6, const A7 &a7, const A8 &a8, const A9 &a9) {
        list9<const A1&, const A2&, const A3&, const A4&, const A5&, const A6&, const A7&, const A8&, const A9&> a(a1, a2, a3, a4, a5, a6, a7, a8, a9);
        return l_(type<result_type>(), f_, a);
    }
    template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8, typename A9>
    result_type operator()(const A1 &a1, const A2 &a2, const A3 &a3, const A4 &a4, const A5 &a5, const A6 &a6, const A7 &a7, const A8 &a8, const A9 &a9) const {
        list9<const A1&, const A2&, const A3&, const A4&, const A5&, const A6&, const A7&, const A8&, const A9&> a(a1, a2, a3, a4, a5, a6, a7, a8, a9);
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
