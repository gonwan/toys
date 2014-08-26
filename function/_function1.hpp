#ifndef _GL_FUNCTION1_HPP
#define _GL_FUNCTION1_HPP


#include <cassert>
#include <stdexcept>
#include "mem_fn.hpp"
#include "functor.hpp"


namespace gl {


namespace detail {

    /* invokers */
    template <typename F, typename A1>
    struct void_function_invoker1 {
        static void invoke(const functor &function_ptr, A1 a1) {
            F f = reinterpret_cast<F>(function_ptr.u.func_ptr);
            f(a1);
        }
    };
    template <typename F, typename R, typename A1>
    struct function_invoker1 {
        static R invoke(const functor &function_ptr, A1 a1) {
            F f = reinterpret_cast<F>(function_ptr.u.func_ptr);
            return f(a1);
        }
    };
    template <typename F, typename A1>
    struct void_function_obj_invoker1 {
        static void invoke(const functor &function_obj_ptr, A1 a1) {
            F *f = reinterpret_cast<F *>(function_obj_ptr.u.obj_ptr);
            (*f)(a1);
        }
    };
    template <typename F, typename R, typename A1>
    struct function_obj_invoker1 {
        static R invoke(const functor &function_obj_ptr, A1 a1) {
            F *f = reinterpret_cast<F *>(function_obj_ptr.u.obj_ptr);
            return (*f)(a1);
        }
    };
    template <typename F, typename A1>
    struct void_function_ref_invoker1 {
        static void invoke(const functor &function_obj_ref_ptr, A1 a1) {
            typedef typename F::type _F;
            _F *f = reinterpret_cast<_F *>(function_obj_ref_ptr.u.obj_ptr);
            (*f)(a1);
        }
    };
    template <typename F, typename R, typename A1>
    struct function_ref_invoker1 {
        static R invoke(const functor &function_obj_ref_ptr, A1 a1) {
            typedef typename F::type _F;
            _F *f = reinterpret_cast<_F *>(function_obj_ref_ptr.u.obj_ptr);
            return (*f)(a1);
        }
    };
    template <typename F, typename A1>
    struct void_member_function_invoker1 {
        static void invoke(const functor &member_ptr, A1 a1) {
            F *f = reinterpret_cast<F *>(member_ptr.u.obj_ptr);
            mem_fn(*f)(a1);
        }
    };
    template <typename F, typename R, typename A1>
    struct member_function_invoker1 {
        static R invoke(const functor &member_ptr, A1 a1) {
            F *f = reinterpret_cast<F *>(member_ptr.u.obj_ptr);
            return mem_fn(*f)(a1);
        }
    };

    /* get invokers */
    template <typename Tag, typename F, typename R, typename A1>
    struct get_function_invoker1 { };

    template <typename F, typename R, typename A1>
    struct get_function_invoker1<function_ptr_tag, F, R, A1> {
        typedef typename if_c<is_void<R>::value,
                void_function_invoker1<F, A1>,
                function_invoker1<F, R, A1> >::type invoker;
    };
    template <typename F, typename R, typename A1>
    struct get_function_invoker1<function_obj_tag, F, R, A1> {
        typedef typename if_c<is_void<R>::value,
                void_function_obj_invoker1<F, A1>,
                function_obj_invoker1<F, R, A1> >::type invoker;
    };
    template <typename F, typename R, typename A1>
    struct get_function_invoker1<function_obj_ref_tag, F, R, A1> {
        typedef typename if_c<is_void<R>::value,
                void_function_ref_invoker1<F, A1>,
                function_ref_invoker1<F, R, A1> >::type invoker;
    };
    template <typename F, typename R, typename A1>
    struct get_function_invoker1<member_function_ptr_tag, F, R, A1> {
        typedef typename if_c<is_void<R>::value,
                void_member_function_invoker1<F, A1>,
                member_function_invoker1<F, R, A1> >::type invoker;
    };

} /* detail */


template <typename R, typename A1>
class function1 {

public:

    typedef R result_type;
    typedef function1 self_type;
    typedef result_type (*invoker_type)(const detail::functor &, A1);

    /* do not use (const _F &f) to enable decay */
    template <typename F>
    function1(F f) : m_invoker(0) {
        assign(f);
    }

    /* empty function */
    function1(int zero) : m_invoker(0) {
        assert(zero == 0);
    }

    /* destructor */
    ~function1() {
        clear();
    }

    bool empty() const {
        return m_invoker == 0;
    }

    result_type operator()(A1 a1) const {
        if (empty()) {
           throw std::runtime_error("bad function call");
        }
        return m_invoker(m_func, a1);
    }

private:

    void clear() {
        m_func.clear();
        m_invoker = 0;
    }

    template <typename F>
    void assign(F &f) {
        typedef typename detail::get_function_tag<F>::type tag;
        typedef typename detail::get_function_invoker1<tag, F, R, A1>::invoker invoker;
        m_func.assign(f, tag());
        m_invoker = &invoker::invoke; /* compile error here if type mismatch */
    }

    detail::functor m_func;
    invoker_type m_invoker;

};


template <typename R, typename A1>
class function<R (A1)> : public function1<R, A1> {

public:

    typedef function1<R, A1> base_type;

    template <typename F>
    function(F f) : base_type(f) { }

    /* we do not need virtual destructors here, since no resource is managed here presently. */
};


} /* gl */


#endif
