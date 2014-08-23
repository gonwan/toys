#ifndef _GL_FUNCTOR_H
#define _GL_FUNCTOR_H


#include "mem_fn.hpp"
#include "type_traits.hpp"
#include "reference_wrapper.hpp"


#define GL_STRINGIZE(X)     GL_DO_STRINGIZE(X)
#define GL_DO_STRINGIZE(X)  #X

#define GL_JOIN(X, Y)       GL_DO_JOIN( X, Y )
#define GL_DO_JOIN(X, Y)    GL_DO_JOIN2(X,Y)
#define GL_DO_JOIN2(X, Y)   X ## Y


namespace gl {


template <typename T>
inline void swap(T &a, T &b) {
    T t = a;
    a = b;
    b = t;
}


struct function_ptr_tag { };
struct function_obj_tag { };
struct function_obj_ref_tag { };
struct member_function_ptr_tag { };


template <typename F>
class get_function_tag {
    typedef typename if_c<is_pointer<F>::value,
            function_ptr_tag, function_obj_tag>::type ptr_or_obj_tag;
    typedef typename if_c<is_member_pointer<F>::value,
            member_function_ptr_tag, ptr_or_obj_tag>::type ptr_or_obj_or_mem_tag;
    typedef typename if_c<(is_reference_wrapper<F>::value),
            function_obj_ref_tag, ptr_or_obj_or_mem_tag>::type or_ref_tag;
public:
    typedef or_ref_tag type;
};


struct functor {
    typedef void *(*cloner_type)(const functor &);
    typedef void (*cleaner_type)(functor &);
    cloner_type m_cloner;
    cleaner_type m_cleaner;
    union _u {
        /* function pointers of all kinds */
        void (*func_ptr)();
        /* pointers to function objects */
        void *obj_ptr;
    } u;
    /* constructor/destructor */
    functor() : m_cloner(0), m_cleaner(0) {
    }
    functor(const functor &other) : m_cloner(other.m_cloner), m_cleaner(other.m_cleaner) {
        if (m_cloner) {
            u.obj_ptr = m_cloner(other);
        } else {
            u.func_ptr = other.u.func_ptr;
        }
    }
    functor &operator=(const functor &other) {
        functor(other).swap(*this);
        return *this;
    }
    ~functor() {
        clear();
    }
    /* swap */
    void swap(functor &other) {
        gl::swap(u, other.u);
        gl::swap(m_cleaner, other.m_cleaner);
    }
    /* clear */
    void clear() {
        if (m_cleaner) {
            m_cleaner(*this);
            m_cleaner = 0;
            m_cloner = 0;
        }
    }
    template <typename F>
    static void *clone_function_obj(const functor &f) {
        return new F(* reinterpret_cast<F *>(f.u.obj_ptr));
    }
    template <typename F>
    static void clean_function_obj(functor &f) {
        delete reinterpret_cast<F *>(f.u.obj_ptr);
    }
    /* assign */
    template <typename F>
    void assign(F f, function_ptr_tag) {
        clear();
        u.func_ptr = reinterpret_cast<void (*)()>(f);
    }
    template <typename F>
    void assign(F f, function_obj_tag) {
        clear();
        u.obj_ptr = new F(f); /* requires copyable */
        m_cloner = functor::clone_function_obj<F>;
        m_cleaner = functor::clean_function_obj<F>;
    }
    template <typename F>
    void assign(F f, function_obj_ref_tag) {
        clear();
        u.obj_ptr = f.get_pointer();
    }
    template <typename F>
    void assign(F f, member_function_ptr_tag) {
        clear();
        assign(mem_fn(f), function_obj_tag()); /* redirect */
    }
};


} /* gl */


#endif
