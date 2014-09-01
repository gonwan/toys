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

/* __not */
template <bool b>
struct __not : true_type { };

template <>
struct __not<true> : false_type { };

/* __and */
template <bool b1, bool b2>
struct __and : false_type { };

template <>
struct __and<true, true> : true_type { };

/* __or */
template <bool b1, bool b2>
struct __or : true_type { };

template <>
struct __or<false, true> : false_type { };

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

/* is_lvalue_reference */
template <typename T>
struct is_lvalue_reference : false_type { };

template <typename T>
struct is_lvalue_reference<T&>: true_type { };

/* is_rvalue_reference */
template <typename T>
struct is_rvalue_reference : false_type { };

template <typename T>
struct is_rvalue_reference<T&&> : public true_type { };

/* is_reference */
template <typename T>
struct is_reference : __or<is_lvalue_reference<T>::value, is_rvalue_reference<T>::value >::type { };

/* remove_reference */
template <typename T>
struct remove_reference {
    typedef T type;
};

template <typename T>
struct remove_reference<T&> {
    typedef T type;
};

template <typename T>
struct remove_reference<T&&> {
    typedef T type;
};

/* add_lvalue_reference */
template <typename T,
     bool = __and<__not<is_reference<T>::value>::value,
                  __not<is_void<T>::value>::value
            >::value,
     bool = is_rvalue_reference<T>::value>
struct add_lvalue_reference_helper {
    typedef T type;
};

template <typename T>
struct add_lvalue_reference_helper<T, true, false> {
    typedef T &type;
};

template <typename T>
struct add_lvalue_reference_helper<T, false, true> {
    typedef typename remove_reference<T>::type &type;
};

template <typename T>
struct add_lvalue_reference : add_lvalue_reference_helper<T> { };

/* add_rvalue_reference */
template <typename T,
         bool = __and<__not<is_reference<T>::value>::value,
                      __not<is_void<T>::value>::value
                >::value>
struct add_rvalue_reference_helper {
    typedef T type;
};

template <typename T>
struct add_rvalue_reference_helper<T, true> {
    typedef T &&type;
};

template <typename T>
struct add_rvalue_reference : public add_rvalue_reference_helper<T> { };

/* is_function */
template <typename T>
struct is_function_helper : false_type { };

template <typename R, typename... TArgs>
struct is_function_helper<R (TArgs...)> : true_type { };

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
