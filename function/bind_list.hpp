#ifndef _GL_BIND_LIST_HPP
#define _GL_BIND_LIST_HPP


#include "type_traits.hpp"
#include "reference_wrapper.hpp"
#include "bind_placeholder.hpp"


namespace gl {


namespace detail {


/* forward declaration */
template <typename R, typename F, typename L>
class bind_t;

/* to hold return type */
template <typename R>
struct type { };

/* result trait */
struct unspecified { };

template <class R, class F>
struct result_traits {
    typedef R type;
};

template <typename F>
struct result_traits<unspecified, F> {
    typedef typename F::result_type type;
};

template <typename F>
struct result_traits<unspecified, reference_wrapper<F> > {
    typedef typename F::result_type type;
};

/* value type */
template <typename T>
class value {
public:
    value(T const & t): t_(t) { }
    T &get() { return t_; }
    const T &get() const { return t_; }
    bool operator==(value const & rhs) const {
        return t_ == rhs.t_;
    }
private:
    T t_;
};

/* unwrapper */
template <typename F>
struct unwrapper {
    inline static F &unwrap(F &f) {
        return f;
    }
};

template <typename F>
struct unwrapper<reference_wrapper<F> > {
    inline static F &unwrap(reference_wrapper<F> &f) {
        return f.get();
    }
};


/* add_value trait */
template <typename T>
struct add_value {
    typedef value<T> type;
};

template <typename T>
struct add_value<value<T> > {
    typedef value<T> type;
};

template <int I> struct add_value<arg<I> > {
    typedef arg<I> type;
};

template <typename T>
struct add_value<reference_wrapper<T> > {
    typedef reference_wrapper<T> type;
};

template <typename R, typename F, typename L>
struct add_value<bind_t<R, F, L> > {
    typedef bind_t<R, F, L> type;
};


/* 0 */
class list0 {

public:

    list0() { }

    template <class T>
    T &operator[](value<T> &v) const {
        return v.get();
    }

    template <class T>
    const T &operator[](const value<T> &v) const {
        return v.get();
    }

    /* reply on the constness of T */
    template <typename T>
    T &operator[](const reference_wrapper<T> &v) const {
        return v.get();
    }

    /* nested bind subexpressions share the placeholders */
    /* These 2 functions use this pointer, they should be copied into every list class... */
    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](const bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    /* no partial specialization for functions, so simply use overload */
    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &) {
        return unwrapper<F>::unwrap(f)();
    }
    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &) const {
        return unwrapper<const F>::unwrap(f)();
    }
    template <typename F, typename A>
    void operator()(type<void>, F &f, A &) {
        unwrapper<F>::unwrap(f)();
    }
    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &) const {
        unwrapper<const F>::unwrap(f)();
    }

};

/* 1 */
template <typename A1>
class list1 : private list0 {

protected:

    typedef list0 base_type;
    A1 a1_;

public:

    explicit list1(A1 a1): a1_( a1 ) { }

    using base_type::operator[];

    A1 operator[](arg<1>) const {
        return a1_;
    }

    /* nested bind subexpressions share the placeholders */
    /* These 2 functions use this pointer, they should be copied into every list class... */
    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](const bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &a) {
        return unwrapper<F>::unwrap(f)(a[a1_]);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &a) const {
        return unwrapper<const F>::unwrap(f)(a[a1_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, F &f, A &a) {
        unwrapper<F>::unwrap(f)(a[a1_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &a) const {
        unwrapper<const F>::unwrap(f)(a[a1_]);
    }

};

/* 2 */
template <typename A1, typename A2>
class list2: private list1<A1> {

protected:

    typedef list1<A1> base_type;
    A2 a2_;
    /* explicit lookup is required in template */
    using base_type::a1_;

public:

    list2(A1 a1, A2 a2): base_type(a1), a2_(a2) { }

    using base_type::operator[];

    A2 operator[](arg<2>) const {
        return a2_;
    }

    /* nested bind subexpressions share the placeholders */
    /* These 2 functions use this pointer, they should be copied into every list class... */
    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](const bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &a) {
        return unwrapper<F>::unwrap(f)(a[a1_], a[a2_]);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &a) const {
        return unwrapper<const F>::unwrap(f)(a[a1_], a[a2_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, F &f, A &a) {
        unwrapper<F>::unwrap(f)(a[a1_], a[a2_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &a) const {
        unwrapper<const F>::unwrap(f)(a[a1_], a[a2_]);
    }

};

/* 3 */
template <typename A1, typename A2, typename A3>
class list3: private list2<A1, A2> {

protected:

    typedef list2<A1, A2> base_type;
    A3 a3_;
    /* explicit lookup is required in template */
    using base_type::a2_;
    using base_type::a1_;

public:

    list3(A1 a1, A2 a2, A3 a3): base_type(a1, a2), a3_(a3) { }

    using base_type::operator[];

    A3 operator[](arg<3>) const {
        return a3_;
    }

    /* nested bind subexpressions share the placeholders */
    /* These 2 functions use this pointer, they should be copied into every list class... */
    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](const bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &a) {
        return unwrapper<F>::unwrap(f)(a[a1_], a[a2_], a[a3_]);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &a) const {
        return unwrapper<const F>::unwrap(f)(a[a1_], a[a2_], a[a3_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, F &f, A &a) {
        unwrapper<F>::unwrap(f)(a[a1_], a[a2_], a[a3_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &a) const {
        unwrapper<const F>::unwrap(f)(a[a1_], a[a2_], a[a3_]);
    }

};

/* 4 */
template <typename A1, typename A2, typename A3, typename A4>
class list4: private list3<A1, A2, A3> {

protected:

    typedef list3<A1, A2, A3> base_type;
    A4 a4_;
    /* explicit lookup is required in template */
    using base_type::a3_;
    using base_type::a2_;
    using base_type::a1_;

public:

    list4(A1 a1, A2 a2, A3 a3, A4 a4): base_type(a1, a2, a3), a4_(a4) { }

    using base_type::operator[];

    A4 operator[](arg<4>) const {
        return a4_;
    }

    /* nested bind subexpressions share the placeholders */
    /* These 2 functions use this pointer, they should be copied into every list class... */
    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](const bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &a) {
        return unwrapper<F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_]);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &a) const {
        return unwrapper<const F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, F &f, A &a) {
        unwrapper<F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &a) const {
        unwrapper<const F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_]);
    }

};

/* 5 */
template <typename A1, typename A2, typename A3, typename A4, typename A5>
class list5: private list4<A1, A2, A3, A4> {

protected:

    typedef list4<A1, A2, A3, A4> base_type;
    A5 a5_;
    /* explicit lookup is required in template */
    using base_type::a4_;
    using base_type::a3_;
    using base_type::a2_;
    using base_type::a1_;

public:

    list5(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5): base_type(a1, a2, a3, a4), a5_(a5) { }

    using base_type::operator[];

    A5 operator[](arg<5>) const {
        return a5_;
    }

    /* nested bind subexpressions share the placeholders */
    /* These 2 functions use this pointer, they should be copied into every list class... */
    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](const bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &a) {
        return unwrapper<F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_]);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &a) const {
        return unwrapper<const F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, F &f, A &a) {
        unwrapper<F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &a) const {
        unwrapper<const F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_]);
    }

};

/* 6 */
template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6>
class list6: private list5<A1, A2, A3, A4, A5> {

protected:

    typedef list5<A1, A2, A3, A4, A5> base_type;
    A6 a6_;
    /* explicit lookup is required in template */
    using base_type::a5_;
    using base_type::a4_;
    using base_type::a3_;
    using base_type::a2_;
    using base_type::a1_;

public:

    list6(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6): base_type(a1, a2, a3, a4, a5), a6_(a6) { }

    using base_type::operator[];

    A6 operator[](arg<6>) const {
        return a6_;
    }

    /* nested bind subexpressions share the placeholders */
    /* These 2 functions use this pointer, they should be copied into every list class... */
    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](const bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &a) {
        return unwrapper<F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_]);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &a) const {
        return unwrapper<const F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, F &f, A &a) {
        unwrapper<F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &a) const {
        unwrapper<const F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_]);
    }

};

/* 7 */
template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7>
class list7: private list6<A1, A2, A3, A4, A5, A6> {

protected:

    typedef list6<A1, A2, A3, A4, A5, A6> base_type;
    A7 a7_;
    /* explicit lookup is required in template */
    using base_type::a6_;
    using base_type::a5_;
    using base_type::a4_;
    using base_type::a3_;
    using base_type::a2_;
    using base_type::a1_;

public:

    list7(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7): base_type(a1, a2, a3, a4, a5, a6), a7_(a7) { }

    using base_type::operator[];

    A7 operator[](arg<7>) const {
        return a7_;
    }

    /* nested bind subexpressions share the placeholders */
    /* These 2 functions use this pointer, they should be copied into every list class... */
    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](const bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &a) {
        return unwrapper<F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_]);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &a) const {
        return unwrapper<const F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, F &f, A &a) {
        unwrapper<F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &a) const {
        return unwrapper<const F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_]);
    }

};

/* 8 */
template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8>
class list8: private list7<A1, A2, A3, A4, A5, A6, A7> {

protected:

    typedef list7<A1, A2, A3, A4, A5, A6, A7> base_type;
    A8 a8_;
    /* explicit lookup is required in template */
    using base_type::a7_;
    using base_type::a6_;
    using base_type::a5_;
    using base_type::a4_;
    using base_type::a3_;
    using base_type::a2_;
    using base_type::a1_;

public:

    list8(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8): base_type(a1, a2, a3, a4, a5, a6, a7), a8_(a8) { }

    using base_type::operator[];

    A8 operator[](arg<8>) const {
        return a8_;
    }

    /* nested bind subexpressions share the placeholders */
    /* These 2 functions use this pointer, they should be copied into every list class... */
    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](const bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &a) {
        return unwrapper<F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_], a[a8_]);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &a) const {
        return unwrapper<const F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_], a[a8_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, F &f, A &a) {
        unwrapper<F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_], a[a8_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &a) const {
        unwrapper<const F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_], a[a8_]);
    }

};

/* 9 */
template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8, typename A9>
class list9: private list8<A1, A2, A3, A4, A5, A6, A7, A8> {

protected:

    typedef list8<A1, A2, A3, A4, A5, A6, A7, A8> base_type;
    A9 a9_;
    /* explicit lookup is required in template */
    using base_type::a8_;
    using base_type::a7_;
    using base_type::a6_;
    using base_type::a5_;
    using base_type::a4_;
    using base_type::a3_;
    using base_type::a2_;
    using base_type::a1_;

public:

    list9(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8, A9 a9): base_type(a1, a2, a3, a4, a5, a6, a7, a8), a9_(a9) { }

    using base_type::operator[];

    A8 operator[](arg<9>) const {
        return a9_;
    }

    /* nested bind subexpressions share the placeholders */
    /* These 2 functions use this pointer, they should be copied into every list class... */
    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](const bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &a) {
        return unwrapper<F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_], a[a8_], a[a9_]);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &a) const {
        return unwrapper<const F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_], a[a8_], a[a9_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, F &f, A &a) {
        unwrapper<F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_], a[a8_], a[a9_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &a) const {
        unwrapper<const F>::unwrap(f)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_], a[a8_], a[a9_]);
    }

};


/* list helpers */
template <typename A1>
struct list_helper1 {
    typedef typename add_value<A1>::type B1;
    typedef list1<B1> type;
};

template <typename A1, typename A2>
struct list_helper2 {
    typedef typename add_value<A1>::type B1;
    typedef typename add_value<A2>::type B2;
    typedef list2<B1, B2> type;
};

template <typename A1, typename A2, typename A3>
struct list_helper3 {
    typedef typename add_value<A1>::type B1;
    typedef typename add_value<A2>::type B2;
    typedef typename add_value<A3>::type B3;
    typedef list3<B1, B2, B3> type;
};

template <typename A1, typename A2, typename A3, typename A4>
struct list_helper4 {
    typedef typename add_value<A1>::type B1;
    typedef typename add_value<A2>::type B2;
    typedef typename add_value<A3>::type B3;
    typedef typename add_value<A4>::type B4;
    typedef list4<B1, B2, B3, B4> type;
};

template <typename A1, typename A2, typename A3, typename A4, typename A5>
struct list_helper5 {
    typedef typename add_value<A1>::type B1;
    typedef typename add_value<A2>::type B2;
    typedef typename add_value<A3>::type B3;
    typedef typename add_value<A4>::type B4;
    typedef typename add_value<A5>::type B5;
    typedef list5<B1, B2, B3, B4, B5> type;
};

template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6>
struct list_helper6 {
    typedef typename add_value<A1>::type B1;
    typedef typename add_value<A2>::type B2;
    typedef typename add_value<A3>::type B3;
    typedef typename add_value<A4>::type B4;
    typedef typename add_value<A5>::type B5;
    typedef typename add_value<A6>::type B6;
    typedef list6<B1, B2, B3, B4, B5, B6> type;
};

template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7>
struct list_helper7 {
    typedef typename add_value<A1>::type B1;
    typedef typename add_value<A2>::type B2;
    typedef typename add_value<A3>::type B3;
    typedef typename add_value<A4>::type B4;
    typedef typename add_value<A5>::type B5;
    typedef typename add_value<A6>::type B6;
    typedef typename add_value<A7>::type B7;
    typedef list7<B1, B2, B3, B4, B5, B6, B7> type;
};

template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8>
struct list_helper8 {
    typedef typename add_value<A1>::type B1;
    typedef typename add_value<A2>::type B2;
    typedef typename add_value<A3>::type B3;
    typedef typename add_value<A4>::type B4;
    typedef typename add_value<A5>::type B5;
    typedef typename add_value<A6>::type B6;
    typedef typename add_value<A7>::type B7;
    typedef typename add_value<A8>::type B8;
    typedef list8<B1, B2, B3, B4, B5, B6, B7, B8> type;
};

template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8, typename A9>
struct list_helper9 {
    typedef typename add_value<A1>::type B1;
    typedef typename add_value<A2>::type B2;
    typedef typename add_value<A3>::type B3;
    typedef typename add_value<A4>::type B4;
    typedef typename add_value<A5>::type B5;
    typedef typename add_value<A6>::type B6;
    typedef typename add_value<A7>::type B7;
    typedef typename add_value<A8>::type B8;
    typedef typename add_value<A9>::type B9;
    typedef list9<B1, B2, B3, B4, B5, B6, B7, B8, B9> type;
};


} /* detail */


} /* gl */


#endif
