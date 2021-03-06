#ifndef _GL_BIND_LIST_HPP
#define _GL_BIND_LIST_HPP


#include "bind_placeholder.hpp"
#include "mem_fn.hpp"
#include "reference_wrapper.hpp"
#include "type_traits.hpp"
#include <tuple>
#include <utility>


#ifndef _GL_DEBUG
#define _GL_PRINT_TYPEID(t)
#else /* _GL_DEBUG */
#include <typeinfo>
#include <iostream>
#ifdef _MSC_VER
void _gl_print_typeid(const char *t, const char *name) {
    std::cout << name << std::endl;
}
#endif /* _MSC_VER */
#ifdef __GNUC__
#include <cxxabi.h>
void _gl_print_typeid(const char *t, const char *name) {
    int s;
    char *p = abi::__cxa_demangle(name, 0, NULL, &s);
    std::cout << "type of " << t << ": " << p << std::endl;
    free(p);
}
#endif /* __GNUC__ */
#define _GL_PRINT_TYPEID(t)     _gl_print_typeid(#t, typeid(t).name())
#endif /* _GL_DEBUG */


namespace gl {


namespace detail {


/* forward declaration */
template <typename R, typename F, typename L>
class bind_t;

/* to hold return type */
template <typename R>
struct type { };

/* result_trait */
struct unspecified { };

template <typename R, typename F>
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
    value(const T &t): t_(t) { }
    T &get() { return t_; }
    const T &get() const { return t_; }
    bool operator==(const value &rhs) const {
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

template <typename R, typename T, typename... TArgs>
struct unwrapper<R(T::*)(TArgs...)> {
    typedef R (T::*F)(TArgs...);
    inline static mf<R, T, TArgs...> &unwrap(F &f) {
        static mf<R, T, TArgs...> _f(f);
        return _f;
    }
};

template <typename R, typename T, typename... TArgs>
struct unwrapper<R(T::*)(TArgs...)const> {
    typedef R (T::*F)(TArgs...) const;
    inline static cmf<R, T, TArgs...> &unwrap(F &f) {
        static cmf<R, T, TArgs...> _f(f);
        return _f;
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

/* value type is used in c++03 */
template <int I>
struct add_value<arg<I> > {
    typedef arg<I> type;
};

/* reference type is used in c++11 */
template <int I>
struct add_value<arg<I> &> {
    typedef arg<I> &type;
};

template <typename T>
struct add_value<reference_wrapper<T> > {
    typedef reference_wrapper<T> type;
};

template <typename R, typename F, typename L>
struct add_value<bind_t<R, F, L> > {
    typedef bind_t<R, F, L> type;
};


template <typename... TArgs>
class list {

    std::tuple<TArgs...> m_tp;

public:
    template <typename... __TArgs>
    explicit list(__TArgs&&... args): m_tp(std::forward<__TArgs>(args)...) { }

    /*
     * The non-const version is added here,
     * since the return value of std::get() depends on the constness of its tuple parameter.
     * The const version simply is for nested bind().
     */
    template <int I>
    typename add_rvalue_reference<
        typename std::tuple_element<I-1, std::tuple<TArgs...> >::type
    >::type
    operator[](arg<I>) {
        typedef typename std::tuple_element<I-1, std::tuple<TArgs...> >::type Tp;
        return std::forward<Tp>((std::get<I-1>(m_tp)));
    }
    /* a rvalue overload may be better? */
    template <int I>
    const typename std::tuple_element<I-1, std::tuple<TArgs...> >::type
    operator[](arg<I>) const {
        return std::get<I-1>(m_tp);
    }

    template <typename T>
    typename add_rvalue_reference<T>::type
    operator[](value<T> &v) const {
        return std::forward<T>(v.get());
    }

    /* reply on the constness of T */
    template <typename T>
    T &operator[](const reference_wrapper<T> &v) const {
        return v.get();
    }

    /* nested bind subexpressions share the placeholders */
    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <typename R, typename F, typename L>
    typename detail::result_traits<R, F>::type operator[](const bind_t<R, F, L> &b) const {
        return b.eval(*this);
    }

    template <int...>
    struct seq { };

    template <int N, int... S>
    struct gens : gens<N-1, N-1, S...> { };

    template <int ...S>
    struct gens<0, S...> {
        typedef seq<S...> type;
    };

    template <typename R, typename F, typename A, int ...S>
    R _op(F &f, A &a, seq<S...>) {
        return unwrapper<F>::unwrap(f)(a[std::get<S>(m_tp)]...);
    }
    template <typename R, typename F, typename A, int ...S>
    R _op(const F &f, A &a, seq<S...>) {
        return unwrapper<const F>::unwrap(f)(a[std::get<S>(m_tp)]...);
    }
    template <typename F, typename A, int ...S>
    void _op(F &f, A &a, seq<S...>) {
        unwrapper<F>::unwrap(f)(a[std::get<S>(m_tp)]...);
    }
    template <typename F, typename A, int ...S>
    void _op(const F &f, A &a, seq<S...>) {
        unwrapper<const F>::unwrap(f)(a[std::get<S>(m_tp)]...);
    }

    template <typename R, typename F, typename A>
    R operator()(type<R>, F &f, A &a) {
        return _op<R>(f, a, typename gens<sizeof...(TArgs)>::type());
    }
    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &a) {
        return _op<R>(f, a, typename gens<sizeof...(TArgs)>::type());
    }
    template <typename R, typename F, typename A>
    R operator()(type<R>, const F &f, A &a) const {
        _op(f, a, typename gens<sizeof...(TArgs)>::type());
    }
    template <typename F, typename A>
    void operator()(type<void>, const F &f, A &a) const {
        _op(f, a, typename gens<sizeof...(TArgs)>::type());
    }

};


/* list_helper */
template <typename... TArgs>
struct list_helper {
    typedef list<typename add_value<TArgs>::type...> type;
};


} /* detail */


} /* gl */


#endif
