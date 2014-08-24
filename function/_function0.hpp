#ifndef _GL_FUNCTION0_HPP
#define _GL_FUNCTION0_HPP


#include <cassert>
#include <stdexcept>
#include "mem_fn.hpp"
#include "functor.hpp"


namespace gl {


namespace detail {

    /* invokers */
    template <typename F>
    struct void_function_invoker0 {
        static void invoke(const functor &function_ptr) {
            F f = reinterpret_cast<F>(function_ptr.u.func_ptr);
            f();
        }
    };
    template <typename F, typename R>
    struct function_invoker0 {
        static R invoke(const functor &function_ptr) {
            F f = reinterpret_cast<F>(function_ptr.u.func_ptr);
            return f();
        }
    };
    template <typename F >
    struct void_function_obj_invoker0 {
        static void invoke(const functor &function_obj_ptr) {
            F *f = reinterpret_cast<F *>(function_obj_ptr.u.obj_ptr);
            (*f)();
        }
    };
    template <typename F, typename R>
    struct function_obj_invoker0 {
        static R invoke(const functor &function_obj_ptr) {
            F *f = reinterpret_cast<F *>(function_obj_ptr.u.obj_ptr);
            return (*f)();
        }
    };
    template <typename F>
    struct void_function_ref_invoker0 {
        static void invoke(const functor &function_obj_ref_ptr) {
            typedef typename F::type _F;
            _F *f = reinterpret_cast<_F *>(function_obj_ref_ptr.u.obj_ptr);
            (*f)();
        }
    };
    template <typename F, typename R>
    struct function_ref_invoker0 {
        static R invoke(const functor &function_obj_ref_ptr) {
            typedef typename F::type _F;
            _F *f = reinterpret_cast<_F *>(function_obj_ref_ptr.u.obj_ptr);
            return (*f)();
        }
    };
    #if 0
    template <typename F>
    struct void_member_function_invoker0 {
        static void invoke(const functor &member_ptr) {
            F *f = reinterpret_cast<F *>(member_ptr.u.obj_ptr);
            mem_fn(*f)();
        }
    };
    template <typename F, typename R>
    struct member_function_invoker0 {
        static R invoke(const functor &member_ptr) {
            F *f = reinterpret_cast<F *>(member_ptr.u.obj_ptr);
            return mem_fn(*f)();
        }
    };
    #endif

    /* get invokers */
    template <typename Tag, typename F, typename R>
    struct get_function_invoker0 { };

    template <typename F, typename R>
    struct get_function_invoker0<function_ptr_tag, F, R> {
        typedef typename if_c<is_void<R>::value,
                void_function_invoker0<F>,
                function_invoker0<F, R> >::type invoker;
    };
    template <typename F, typename R>
    struct get_function_invoker0<function_obj_tag, F, R> {
        typedef typename if_c<is_void<R>::value,
                void_function_obj_invoker0<F>,
                function_obj_invoker0<F, R> >::type invoker;
    };
    template <typename F, typename R>
    struct get_function_invoker0<function_obj_ref_tag, F, R> {
        typedef typename if_c<is_void<R>::value,
                void_function_ref_invoker0<F>,
                function_ref_invoker0<F, R> >::type invoker;
    };
    #if 0
    template <typename F, typename R>
    struct get_function_invoker0<member_ptr_tag, F, R> {
        typedef typename if_c<is_void<R>::value,
                function_void_member_invoker0<F>,
                function_member_invoker0<F, R> >::type invoker;
    };
    #endif

} /* detail */


template <typename R>
class function0 {

public:

    typedef R result_type;
    typedef function0 self_type;
    typedef result_type (*invoker_type)(const functor &);

    /* do not use (const _F &f) to enable decay */
    template <typename F>
    function0(F f) : m_invoker(0) {
        assign(f);
    }

    /* empty function */
    function0(int zero) : m_invoker(0) {
        assert(zero == 0);
    }

    /* destructor */
    ~function0() {
        clear();
    }

    bool empty() const {
        return m_invoker == 0;
    }

    result_type operator()() const {
        if (empty()) {
           throw std::runtime_error("bad function call");
        }
        return m_invoker(m_func);
    }

private:

    void clear() {
        m_func.clear();
        m_invoker = 0;
    }

    template <typename F>
    void assign(F &f) {
        typedef typename get_function_tag<F>::type tag;
        typedef typename detail::get_function_invoker0<tag, F, R>::invoker invoker;
        m_func.assign(f, tag());
        m_invoker = &invoker::invoke; /* compile error here if type mismatch */
    }

    functor m_func;
    invoker_type m_invoker;

};


template <typename R>
class function<R (void)> : public function0<R> {

public:

    typedef function0<R> base_type;

    template <typename F>
    function(F f) : base_type(f) { }

    /* we do not need virtual destructors here, since no resource is managed here presently. */
};


} /* gl */


#endif
