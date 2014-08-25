#ifndef _GL_BIND_LIST_HPP
#define _GL_BIND_LIST_HPP


#include "type_traits.hpp"
#include "reference_wrapper.hpp"
#include "bind_placeholder.hpp"
#include <iostream>
#include <typeinfo>


namespace gl {


namespace detail {
template<typename T> class value
{
public:

    value(T const & t): t_(t) {}

    T & get() { return t_; }
    T const & get() const { return t_; }

    bool operator==(value const & rhs) const
    {
        return t_ == rhs.t_;
    }

private:

    T t_;
};


template<typename R, typename F, typename L> class bind_t;







template <typename F>
struct unwrapper {
    static inline F & unwrap( F & f, long )
    {
        return f;
    }

    template<typename F2> static inline F2 & unwrap( reference_wrapper<F2> rf, int )
    {
        return rf.get();
    }

    /*template<typename R, typename T> static inline _mfi::dm<R, T> unwrap( R T::* pm, int )
    {
        return _mfi::dm<R, T>( pm );
    }*/
};

template <typename R>
struct type {

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

    //template <typename R, typename F, typename L>
    //typename result_traits<R, F>::type operator[] (bind_t<R, F, L> & b) const { return b.eval(*this); }

    //template <typename R, typename F, typename L>
    //typename result_traits<R, F>::type operator[] (bind_t<R, F, L> const & b) const { return b.eval(*this); }

    /* no partial specialization for functions, so simply use overload */
    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &) {
        return unwrapper<F>::unwrap(f, 0)();
    }
    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &) const {
        return unwrapper<const F>::unwrap(f, 0)();
    }
    template <typename F, typename A>
    void operator()(type<void>, F &f, A &) {
        unwrapper<F>::unwrap(f, 0)();
    }
    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &) const {
        unwrapper<F const>::unwrap(f, 0)();
    }

    /* visitor */
    /*template<typename V> void accept(V &) const
    {
    }*/

    bool operator==(list0 const &) const {
        return true;
    }

};

/* 1 */
template <typename A1>
class list1 : private list0
{

protected:

    typedef list0 base_type;
    A1 a1_;

public:

    explicit list1(A1 a1): a1_( a1 ) { }

    using base_type::operator[];

    A1 operator[](arg<1>) const {
        return a1_;
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &a) {
        return unwrapper<F>::unwrap(f, 0)(a[a1_]);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &a) const {
        return unwrapper<const F>::unwrap(f, 0)(a[a1_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, F &f, A &a) {
        unwrapper<F>::unwrap(f, 0)(a[a1_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &a) const {
        unwrapper<F const>::unwrap(f, 0)(a[a1_]);
    }

    template <typename V> void accept(V & v) const
    {
        //base_type::accept(v);
    }

    bool operator==(list1 const & rhs) const
    {
        //return ref_compare(base_type::a1_, rhs.a1_, 0);
    }

};

/* 2 */
template <typename A1, typename A2>
class list2: private list1<A1>
{
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

    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &a) {
        return unwrapper<F>::unwrap(f, 0)(a[a1_], a[a2_]);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &a) const {
        return unwrapper<const F>::unwrap(f, 0)(a[a1_], a[a2_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, F &f, A &a) {
        /* a is list1 */
        /*std::cout << typeid(base_type::a1_).name() << std::endl;
        std::cout << typeid(a2_).name() << std::endl;
        std::cout << typeid(a[base_type::a1_]).name() << std::endl;
        std::cout << typeid(a[a2_]).name() << std::endl;*/
        unwrapper<F>::unwrap(f, 0)(a[a1_], a[a2_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &a) const {
        unwrapper<const F>::unwrap(f, 0)(a[a1_], a[a2_]);
    }

    template<typename V> void accept(V & v) const
    {
        //base_type::accept(v);
    }

    bool operator==(list2 const & rhs) const
    {
        //return ref_compare(base_type::a1_, rhs.a1_, 0) && ref_compare(base_type::a2_, rhs.a2_, 0);
    }

};

/* 3 */
template <typename A1, typename A2, typename A3>
class list3: private list2<A1, A2>
{
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

    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &a) {
        return unwrapper<F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_]);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &a) const {
        return unwrapper<const F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, F &f, A &a) {
        unwrapper<F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &a) const {
        unwrapper<const F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_]);
    }

    template<typename V> void accept(V & v) const
    {
        //base_type::accept(v);
    }

    bool operator==(list3 const & rhs) const
    {
        return false;
    }

};

/* 4 */
template <typename A1, typename A2, typename A3, typename A4>
class list4: private list3<A1, A2, A3>
{
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

    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &a) {
        return unwrapper<F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_]);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &a) const {
        return unwrapper<const F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, F &f, A &a) {
        unwrapper<F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &a) const {
        unwrapper<const F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_]);
    }

    template<typename V> void accept(V & v) const
    {
        //base_type::accept(v);
    }

    bool operator==(list4 const & rhs) const
    {
        return false;
    }

};

/* 5 */
template <typename A1, typename A2, typename A3, typename A4, typename A5>
class list5: private list4<A1, A2, A3, A4>
{
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

    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &a) {
        return unwrapper<F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_]);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &a) const {
        return unwrapper<const F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, F &f, A &a) {
        unwrapper<F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &a) const {
        unwrapper<const F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_]);
    }

    template<typename V> void accept(V & v) const
    {
        //base_type::accept(v);
    }

    bool operator==(list5 const & rhs) const
    {
        return false;
    }

};

/* 6 */
template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6>
class list6: private list5<A1, A2, A3, A4, A5>
{
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

    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &a) {
        return unwrapper<F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_]);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &a) const {
        return unwrapper<const F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, F &f, A &a) {
        unwrapper<F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &a) const {
        unwrapper<const F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_]);
    }

    template<typename V> void accept(V & v) const
    {
        //base_type::accept(v);
    }

    bool operator==(list6 const & rhs) const
    {
        return false;
    }

};

/* 7 */
template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7>
class list7: private list6<A1, A2, A3, A4, A5, A6>
{
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

    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &a) {
        return unwrapper<F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_]);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &a) const {
        return unwrapper<const F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, F &f, A &a) {
        unwrapper<F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &a) const {
        unwrapper<const F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_]);
    }

    template<typename V> void accept(V & v) const
    {
        //base_type::accept(v);
    }

    bool operator==(list7 const & rhs) const
    {
        return false;
    }

};

/* 8 */
template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8>
class list8: private list7<A1, A2, A3, A4, A5, A6, A7>
{
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

    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &a) {
        return unwrapper<F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_], a[a8_]);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &a) const {
        return unwrapper<const F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_], a[a8_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, F &f, A &a) {
        unwrapper<F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_], a[a8_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &a) const {
        unwrapper<const F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_], a[a8_]);
    }

    template<typename V> void accept(V & v) const
    {
        //base_type::accept(v);
    }

    bool operator==(list8 const & rhs) const
    {
        return false;
    }

};

/* 9 */
template <typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8, typename A9>
class list9: private list7<A1, A2, A3, A4, A5, A6, A7>
{
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

    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &a) {
        return unwrapper<F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_], a[a8_], a[a9_]);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &a) const {
        return unwrapper<const F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_], a[a8_], a[a9_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, F &f, A &a) {
        unwrapper<F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_], a[a8_], a[a9_]);
    }

    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &a) const {
        unwrapper<const F>::unwrap(f, 0)(a[a1_], a[a2_], a[a3_], a[a4_], a[a5_], a[a6_], a[a7_], a[a8_], a[a9_]);
    }

    template<typename V> void accept(V & v) const
    {
        //base_type::accept(v);
    }

    bool operator==(list9 const & rhs) const
    {
        return false;
    }

};





} /* detail */


} /* gl */


#endif
