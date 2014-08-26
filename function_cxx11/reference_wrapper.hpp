#ifndef _GL_REFERENCE_WRAPPER_HPP
#define _GL_REFERENCE_WRAPPER_HPP


#include "type_traits.hpp"


namespace gl {


template <typename T>
inline T* addressof(T &t) {
    return reinterpret_cast<T *>(&const_cast<char&>(reinterpret_cast<const volatile char&>(t)));
}


template <typename T>
class reference_wrapper
{
public:
    typedef T type;
    explicit reference_wrapper(T &t): t_(addressof(t)) { }
    operator T &() const { return *t_; }
    T &get() const { return *t_; }
    T *get_pointer() const { return t_; }
private:
    T *t_;
};

template <typename T>
reference_wrapper<T> ref(T &t) {
    return reference_wrapper<T>(t);
}

template <typename T>
reference_wrapper<const T> cref(const T &t) {
    return reference_wrapper<const T>(t);
}


template <typename T>
struct is_reference_wrapper_helper : false_type { };

template <typename T>
struct is_reference_wrapper_helper<reference_wrapper<T> > : true_type { };

template <typename T>
struct is_reference_wrapper : is_reference_wrapper_helper<typename remove_cv<T>::type> { };


} /* gl */


#endif
