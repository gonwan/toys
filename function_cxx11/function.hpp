#ifndef _GL_FUNCTION_HPP
#define _GL_FUNCTION_HPP


#include <cassert>
#include <stdexcept>
#include <utility>
#include "mem_fn.hpp"
#include "functor.hpp"


namespace gl {


namespace detail {

    /* invokers */
    template <typename F, typename... TArgs>
    struct void_function_invoker {
        static void invoke(const functor &function_ptr, TArgs... args) {
            F f = reinterpret_cast<F>(function_ptr.u.func_ptr);
            f(args...);
        }
    };
    template <typename F, typename R, typename... TArgs>
    struct function_invoker {
        static R invoke(const functor &function_ptr, TArgs... args) {
            F f = reinterpret_cast<F>(function_ptr.u.func_ptr);
            return f(args...);
        }
    };
    template <typename F, typename... TArgs>
    struct void_function_obj_invoker {
        static void invoke(const functor &function_obj_ptr, TArgs... args) {
            F *f = reinterpret_cast<F *>(function_obj_ptr.u.obj_ptr);
            (*f)(args...);
        }
    };
    template <typename F, typename R, typename... TArgs>
    struct function_obj_invoker {
        static R invoke(const functor &function_obj_ptr, TArgs... args) {
            F *f = reinterpret_cast<F *>(function_obj_ptr.u.obj_ptr);
            return (*f)(args...);
        }
    };
    template <typename F, typename... TArgs>
    struct void_function_ref_invoker {
        static void invoke(const functor &function_obj_ref_ptr, TArgs... args) {
            typedef typename F::type _F;
            _F *f = reinterpret_cast<_F *>(function_obj_ref_ptr.u.obj_ptr);
            (*f)(args...);
        }
    };
    template <typename F, typename R, typename... TArgs>
    struct function_ref_invoker {
        static R invoke(const functor &function_obj_ref_ptr, TArgs... args) {
            typedef typename F::type _F;
            _F *f = reinterpret_cast<_F *>(function_obj_ref_ptr.u.obj_ptr);
            return (*f)(args...);
        }
    };
    template <typename F, typename... TArgs>
    struct void_member_function_invoker {
        static void invoke(const functor &member_ptr, TArgs... args) {
            F *f = reinterpret_cast<F *>(member_ptr.u.obj_ptr);
            mem_fn(*f)(args...);
        }
    };
    template <typename F, typename R, typename... TArgs>
    struct member_function_invoker {
        static R invoke(const functor &member_ptr, TArgs... args) {
            F *f = reinterpret_cast<F *>(member_ptr.u.obj_ptr);
            return mem_fn(*f)(args...);
        }
    };

    /* get invokers */
    template <typename Tag, typename F, typename R, typename... TArgs>
    struct get_function_invoker { };

    template <typename F, typename R, typename... TArgs>
    struct get_function_invoker<function_ptr_tag, F, R, TArgs...> {
        typedef typename if_c<is_void<R>::value,
                void_function_invoker<F, TArgs...>,
                function_invoker<F, R, TArgs...> >::type invoker;
    };
    template <typename F, typename R, typename... TArgs>
    struct get_function_invoker<function_obj_tag, F, R, TArgs...> {
        typedef typename if_c<is_void<R>::value,
                void_function_obj_invoker<F, TArgs...>,
                function_obj_invoker<F, R, TArgs...> >::type invoker;
    };
    template <typename F, typename R, typename... TArgs>
    struct get_function_invoker<function_obj_ref_tag, F, R, TArgs...> {
        typedef typename if_c<is_void<R>::value,
                void_function_ref_invoker<F, TArgs...>,
                function_ref_invoker<F, R, TArgs...> >::type invoker;
    };
    template <typename F, typename R, typename... TArgs>
    struct get_function_invoker<member_function_ptr_tag, F, R, TArgs...> {
        typedef typename if_c<is_void<R>::value,
                void_member_function_invoker<F, TArgs...>,
                member_function_invoker<F, R, TArgs...> >::type invoker;
    };

} /* detail */


template <typename Sig>
class function;


template <typename R, typename... TArgs>
class function<R (TArgs...)> {

public:

    typedef R result_type;
    typedef function self_type;
    typedef result_type (*invoker_type)(const detail::functor &, TArgs...);

    /* do not use (const _F &f) to enable decay */
    template <typename F>
    function(F f) : m_invoker(0) {
        assign(f);
    }

    /* empty function */
    function(int zero) : m_invoker(0) {
        assert(zero == 0);
    }

    /* destructor */
    ~function() {
        clear();
    }

    bool empty() const {
        return m_invoker == 0;
    }

    result_type operator()(TArgs&&... args) const {
        if (empty()) {
           throw std::runtime_error("bad function call");
        }
        return m_invoker(m_func, std::forward<TArgs>(args)...);
    }

private:

    void clear() {
        m_func.clear();
        m_invoker = 0;
    }

    template <typename F>
    void assign(F &f) {
        typedef typename detail::get_function_tag<F>::type tag;
        typedef typename detail::get_function_invoker<tag, F, R, TArgs...>::invoker invoker;
        m_func.assign(f, tag());
        m_invoker = &invoker::invoke; /* compile error here if type mismatch */
    }

    detail::functor m_func;
    invoker_type m_invoker;

};


} /* gl */


#endif
