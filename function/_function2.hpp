#ifndef _GL_FUNCTION2_HPP
#define _GL_FUNCTION2_HPP


#include <cassert>
#include <stdexcept>
#include "mem_fn.hpp"
#include "functor.hpp"


namespace gl {


namespace detail {

    /* invokers */
    template <typename F, typename A1, typename A2>
    struct function_void_function_invoker2 {
        static void invoke(const functor &function_ptr, A1 a1, A2 a2) {
            F f = reinterpret_cast<F>(function_ptr.u.func_ptr);
            f(a1, a2);
        }
    };
    template <typename F, typename R, typename A1, typename A2>
    struct function_function_invoker2 {
        static R invoke(const functor &function_ptr, A1 a1, A2 a2) {
            F f = reinterpret_cast<F>(function_ptr.u.func_ptr);
            return f(a1, a2);
        }
    };
    template<typename F, typename A1, typename A2>
    struct function_void_function_obj_invoker2 {
        static void invoke(const functor &function_obj_ptr, A1 a1, A2 a2) {
            F *f = reinterpret_cast<F *>(function_obj_ptr.u.obj_ptr);
            (*f)(a1, a2);
        }
    };
    template<typename F, typename R, typename A1, typename A2>
    struct function_function_obj_invoker2 {
        static R invoke(const functor &function_obj_ptr, A1 a1, A2 a2) {
            F *f = reinterpret_cast<F *>(function_obj_ptr.u.obj_ptr);
            return (*f)(a1, a2);
        }
    };
    template<typename F, typename A1, typename A2>
    struct function_void_member_function_invoker2 {
        static void invoke(const functor &member_ptr, A1 a1, A2 a2) {
            F *f = reinterpret_cast<F *>(member_ptr.u.obj_ptr);
            mem_fn(*f)(a1, a2);
        }
    };
    template<typename F, typename R, typename A1, typename A2>
    struct function_member_function_invoker2 {
        static R invoke(const functor &member_ptr, A1 a1, A2 a2) {
            F *f = reinterpret_cast<F *>(member_ptr.u.obj_ptr);
            return mem_fn(*f)(a1, a2);
        }
    };
    template<typename F, typename A1, typename A2>
    struct function_void_function_obj_ref_invoker2 {
        static void invoke(const functor &function_obj_ref_ptr, A1 a1, A2 a2) {
            typedef typename F::type _F;
            _F *f = reinterpret_cast<_F *>(function_obj_ref_ptr.u.obj_ptr);
            (*f)(a1, a2);
        }
    };
    template<typename F, typename R, typename A1, typename A2>
    struct function_function_obj_ref_invoker2 {
        static R invoke(const functor &function_obj_ref_ptr, A1 a1, A2 a2) {
            typedef typename F::type _F;
            _F *f = reinterpret_cast<_F *>(function_obj_ref_ptr.u.obj_ptr);
            return (*f)(a1, a2);
        }
    };

    /* get invokers */
    template <typename Tag, typename F, typename R, typename A1, typename A2>
    struct get_function_invoker2 { };

    template <typename F, typename R, typename A1, typename A2>
    struct get_function_invoker2<function_ptr_tag, F, R, A1, A2> {
        typedef typename if_c<is_void<R>::value,
                function_void_function_invoker2<F, A1, A2>,
                function_function_invoker2<F, R, A1, A2> >::type invoker;
    };
    template <typename F, typename R, typename A1, typename A2>
    struct get_function_invoker2<function_obj_tag, F, R, A1, A2> {
        typedef typename if_c<is_void<R>::value,
                function_void_function_obj_invoker2<F, A1, A2>,
                function_function_obj_invoker2<F, R, A1, A2> >::type invoker;
    };
    template <typename F, typename R, typename A1, typename A2>
    struct get_function_invoker2<member_function_ptr_tag, F, R, A1, A2> {
        typedef typename if_c<is_void<R>::value,
                function_void_member_function_invoker2<F, A1, A2>,
                function_member_function_invoker2<F, R, A1, A2> >::type invoker;
    };
    template <typename F, typename R, typename A1, typename A2>
    struct get_function_invoker2<function_obj_ref_tag, F, R, A1, A2> {
        typedef typename if_c<is_void<R>::value,
                function_void_function_obj_ref_invoker2<F, A1, A2>,
                function_function_obj_ref_invoker2<F, R, A1, A2> >::type invoker;
    };

} /* detail */


template <typename R, typename A1, typename A2>
class function2 {

public:

    typedef R result_type;
    typedef function2 self_type;
    typedef result_type (*invoker_type)(const functor &, A1, A2);

    /* do not use (const _F &f) to enable decay */
    template <typename F>
    function2(F f) : m_invoker(0) {
        assign(f);
    }

    /* empty function */
    function2(int zero) : m_invoker(0) {
        assert(zero == 0);
    }

    /* destructor */
    ~function2() {
        clear();
    }

    bool empty() const {
        return m_invoker == 0;
    }

    result_type operator()(A1 a1, A2 a2) const {
        if (this->empty()) {
           throw std::runtime_error("bad function call");
        }
        return m_invoker(m_func, a1, a2);
    }

private:

    void clear() {
        m_func.clear();
        m_invoker = 0;
    }

    template <typename F>
    void assign(F &f) {
        typedef typename get_function_tag<F>::type tag;
        typedef typename detail::get_function_invoker2<tag, F, R, A1, A2>::invoker invoker;
        m_func.assign(f, tag());
        m_invoker = &invoker::invoke;
    }

    functor m_func;
    invoker_type m_invoker;

};


template <typename R, typename A1, typename A2>
struct function<R (A1, A2)> : public function2<R, A1, A2> {

public:

    typedef function2<R, A1, A2> base_type;

    template <typename F>
    function(F f) : base_type(f) {
    }

    /* we do not need virtual destructors here, since no resource is managed here presently. */
};


} /* namespace */


#endif
