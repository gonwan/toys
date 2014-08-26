#ifndef _GL_TYPE_TRAITS_H
#define _GL_TYPE_TRAITS_H


namespace gl {


/* if_c */
template <bool C, typename T1, typename T2>
struct if_c {
    typedef T1 type;
};

template <typename T1, typename T2>
struct if_c<false, T1, T2> {
    typedef T2 type;
};

/* enable_if_c */
template <bool B, typename T = void>
struct enable_if_c {
    typedef T type;
};

template <typename T>
struct enable_if_c<false, T> {
};

/* enable_if */
template <typename Cond, typename T = void>
struct enable_if : public enable_if_c<Cond::value, T> {
};

/* add_const */
template <typename T>
struct add_const {
    typedef const T type;
};

/* add_volatile */
template <typename T>
struct add_volatile {
    typedef volatile T type;
};

/* add_cv */
template <typename T>
struct add_cv {
    typedef typename add_volatile<typename add_const<T>::type>::type type;
};

/* remove_const */
template <typename T>
struct remove_const {
    typedef T type;
};

template <typename T>
struct remove_const<const T> {
    typedef T type;
};

/* remove_volatile */
template <typename T>
struct remove_volatile {
    typedef T type;
};

template <typename T>
struct remove_volatile<volatile T> {
    typedef T type;
};

/* remove_cv */
template <typename T>
struct remove_cv {
    typedef typename remove_volatile<typename remove_const<T>::type>::type type;
};

/* integral_constant */
template <typename T, T v>
struct integral_constant {
    static const T value = v;
    typedef T value_type;
    typedef integral_constant type;
    operator value_type() const { return value; }
    value_type operator()() const { return value; }
};

typedef integral_constant<bool, true> true_type;
typedef integral_constant<bool, false> false_type;

/* is_same */
template <typename T, typename U>
struct is_same : false_type { };

template <typename T>
struct is_same<T, T> : true_type { };

/* is_void */
template <typename T>
struct is_void : false_type { };

template <>
struct is_void<void> : true_type { };

/* is_const */
template <typename T>
struct is_const : false_type { };

template <typename T>
struct is_const<const T> : true_type { };

/* is_volatile */
template <typename T>
struct is_volatile : false_type { };

template <typename T>
struct is_volatile<volatile T> : true_type { };

/* is_pointer */
template <typename T>
struct is_pointer_helper : false_type { };

template <typename T>
struct is_pointer_helper<T *> : true_type { };

template <typename T>
struct is_pointer : is_pointer_helper<typename remove_cv<T>::type> { };

/* is_reference */
template <typename T>
struct is_reference : false_type { };

template <typename T>
struct is_reference<T &> : true_type { };

/* is_function */
template <typename>
struct is_function_helper : false_type { };

template <typename R>
struct is_function_helper<R ()> : true_type { };
template <typename R, typename A1>
struct is_function_helper<R (A1)> : true_type { };
template <typename R, typename A1, typename A2>
struct is_function_helper<R (A1, A2)> : true_type { };
template <typename R, typename A1, typename A2, typename A3>
struct is_function_helper<R (A1, A2, A3)> : true_type { };
template <typename R, typename A1, typename A2, typename A3, typename A4>
struct is_function_helper<R (A1, A2, A3, A4)> : true_type { };
template <typename R, typename A1, typename A2, typename A3, typename A4, typename A5>
struct is_function_helper<R (A1, A2, A3, A4, A5)> : true_type { };
template <typename R, typename A1, typename A2, typename A3, typename A4, typename A5, typename A6>
struct is_function_helper<R (A1, A2, A3, A4, A5, A6)> : true_type { };
template <typename R, typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7>
struct is_function_helper<R (A1, A2, A3, A4, A5, A6, A7)> : true_type { };
template <typename R, typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8>
struct is_function_helper<R (A1, A2, A3, A4, A5, A6, A7, A8)> : true_type { };
template <typename R, typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8, typename A9>
struct is_function_helper<R (A1, A2, A3, A4, A5, A6, A7, A8, A9)> : true_type { };

template <typename T>
struct is_function : is_function_helper<typename remove_cv<T>::type> { };

/* is_member_pointer */
template <typename T>
struct is_member_pointer_helper : false_type { };

template <typename T, typename U>
struct is_member_pointer_helper<T U::*> : true_type { };

template <typename T>
struct is_member_pointer: is_member_pointer_helper<typename remove_cv<T>::type> { };

/* is_member_function_pointer */
template <typename T>
struct is_member_function_pointer_helper : false_type { };

template <typename T, typename U>
struct is_member_function_pointer_helper<T U::*> : is_function<T> { };

template <typename T>
struct is_member_function_pointer : is_member_function_pointer_helper<typename remove_cv<T>::type> { };


} /* gl */


#endif
