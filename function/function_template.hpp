#include <cassert>
#include <stdexcept>
#include "mem_fn.hpp"
#include "functor.hpp"


#define GL_FUNC_NAME  \
    GL_JOIN(function, GL_FUNC_PARAMS_NUM)
#define GL_FUNC_VOID_FUNCTION_INVOKER  \
    GL_JOIN(void_function_invoker, GL_FUNC_PARAMS_NUM)
#define GL_FUNC_FUNCTION_INVOKER  \
    GL_JOIN(function_invoker, GL_FUNC_PARAMS_NUM)
#define GL_FUNC_VOID_FUNCTION_OBJ_INVOKER  \
    GL_JOIN(void_function_obj_invoker, GL_FUNC_PARAMS_NUM)
#define GL_FUNC_FUNCTION_OBJ_INVOKER  \
    GL_JOIN(function_obj_invoker, GL_FUNC_PARAMS_NUM)
#define GL_FUNC_VOID_FUNCTION_REF_INVOKER  \
    GL_JOIN(void_function_ref_invoker, GL_FUNC_PARAMS_NUM)
#define GL_FUNC_FUNCTION_REF_INVOKER  \
    GL_JOIN(function_ref_invoker, GL_FUNC_PARAMS_NUM)
#define GL_FUNC_VOID_MEMBER_FUNCTION_INVOKER  \
    GL_JOIN(void_member_function_invoker, GL_FUNC_PARAMS_NUM)
#define GL_FUNC_MEMBER_FUNCTION_INVOKER  \
    GL_JOIN(member_function_invoker, GL_FUNC_PARAMS_NUM)
#define GL_FUNC_GET_FUNCTION_INVOKER  \
    GL_JOIN(get_function_invoker, GL_FUNC_PARAMS_NUM)


namespace gl {


namespace detail {

    /* invokers */
    template <typename F GL_FUNC_COMMA GL_FUNC_TEMPLATE_PARAMS>
    struct GL_FUNC_VOID_FUNCTION_INVOKER {
        static void invoke(const functor &function_ptr GL_FUNC_COMMA GL_FUNC_PARAMS) {
            F f = reinterpret_cast<F>(function_ptr.u.func_ptr);
            f(GL_FUNC_ARGS);
        }
    };
    template <typename F, typename R GL_FUNC_COMMA GL_FUNC_TEMPLATE_PARAMS>
    struct GL_FUNC_FUNCTION_INVOKER {
        static R invoke(const functor &function_ptr GL_FUNC_COMMA GL_FUNC_PARAMS) {
            F f = reinterpret_cast<F>(function_ptr.u.func_ptr);
            return f(GL_FUNC_ARGS);
        }
    };
    template <typename F GL_FUNC_COMMA GL_FUNC_TEMPLATE_PARAMS>
    struct GL_FUNC_VOID_FUNCTION_OBJ_INVOKER {
        static void invoke(const functor &function_obj_ptr GL_FUNC_COMMA GL_FUNC_PARAMS) {
            F *f = reinterpret_cast<F *>(function_obj_ptr.u.obj_ptr);
            (*f)(GL_FUNC_ARGS);
        }
    };
    template <typename F, typename R GL_FUNC_COMMA GL_FUNC_TEMPLATE_PARAMS>
    struct GL_FUNC_FUNCTION_OBJ_INVOKER {
        static R invoke(const functor &function_obj_ptr GL_FUNC_COMMA GL_FUNC_PARAMS) {
            F *f = reinterpret_cast<F *>(function_obj_ptr.u.obj_ptr);
            return (*f)(GL_FUNC_ARGS);
        }
    };
    template <typename F GL_FUNC_COMMA GL_FUNC_TEMPLATE_PARAMS>
    struct GL_FUNC_VOID_FUNCTION_REF_INVOKER {
        static void invoke(const functor &function_obj_ref_ptr GL_FUNC_COMMA GL_FUNC_PARAMS) {
            typedef typename F::type _F;
            _F *f = reinterpret_cast<_F *>(function_obj_ref_ptr.u.obj_ptr);
            (*f)(GL_FUNC_ARGS);
        }
    };
    template <typename F, typename R GL_FUNC_COMMA GL_FUNC_TEMPLATE_PARAMS>
    struct GL_FUNC_FUNCTION_REF_INVOKER {
        static R invoke(const functor &function_obj_ref_ptr GL_FUNC_COMMA GL_FUNC_PARAMS) {
            typedef typename F::type _F;
            _F *f = reinterpret_cast<_F *>(function_obj_ref_ptr.u.obj_ptr);
            return (*f)(GL_FUNC_ARGS);
        }
    };
#if GL_FUNC_PARAMS_NUM > 0
    template <typename F GL_FUNC_COMMA GL_FUNC_TEMPLATE_PARAMS>
    struct GL_FUNC_VOID_MEMBER_FUNCTION_INVOKER {
        static void invoke(const functor &member_ptr GL_FUNC_COMMA GL_FUNC_PARAMS) {
            F *f = reinterpret_cast<F *>(member_ptr.u.obj_ptr);
            mem_fn(*f)(GL_FUNC_ARGS);
        }
    };
    template <typename F, typename R GL_FUNC_COMMA GL_FUNC_TEMPLATE_PARAMS>
    struct GL_FUNC_MEMBER_FUNCTION_INVOKER {
        static R invoke(const functor &member_ptr GL_FUNC_COMMA GL_FUNC_PARAMS) {
            F *f = reinterpret_cast<F *>(member_ptr.u.obj_ptr);
            return mem_fn(*f)(GL_FUNC_ARGS);
        }
    };
#endif

    /* get invokers */
    template <typename Tag, typename F, typename R GL_FUNC_COMMA GL_FUNC_TEMPLATE_PARAMS>
    struct GL_FUNC_GET_FUNCTION_INVOKER { };

    template <typename F, typename R GL_FUNC_COMMA GL_FUNC_TEMPLATE_PARAMS>
    struct GL_FUNC_GET_FUNCTION_INVOKER<function_ptr_tag, F, R GL_FUNC_COMMA GL_FUNC_TEMPLATE_ARGS> {
        typedef typename if_c<is_void<R>::value,
                GL_FUNC_VOID_FUNCTION_INVOKER<F GL_FUNC_COMMA GL_FUNC_TEMPLATE_ARGS>,
                GL_FUNC_FUNCTION_INVOKER<F, R GL_FUNC_COMMA GL_FUNC_TEMPLATE_ARGS> >::type invoker;
    };
    template <typename F, typename R GL_FUNC_COMMA GL_FUNC_TEMPLATE_PARAMS>
    struct GL_FUNC_GET_FUNCTION_INVOKER<function_obj_tag, F, R GL_FUNC_COMMA GL_FUNC_TEMPLATE_ARGS> {
        typedef typename if_c<is_void<R>::value,
                GL_FUNC_VOID_FUNCTION_OBJ_INVOKER<F GL_FUNC_COMMA GL_FUNC_TEMPLATE_ARGS>,
                GL_FUNC_FUNCTION_OBJ_INVOKER<F, R GL_FUNC_COMMA GL_FUNC_TEMPLATE_ARGS> >::type invoker;
    };
    template <typename F, typename R GL_FUNC_COMMA GL_FUNC_TEMPLATE_PARAMS>
    struct GL_FUNC_GET_FUNCTION_INVOKER<function_obj_ref_tag, F, R GL_FUNC_COMMA GL_FUNC_TEMPLATE_ARGS> {
        typedef typename if_c<is_void<R>::value,
                GL_FUNC_VOID_FUNCTION_REF_INVOKER<F GL_FUNC_COMMA GL_FUNC_TEMPLATE_ARGS>,
                GL_FUNC_FUNCTION_REF_INVOKER<F, R GL_FUNC_COMMA GL_FUNC_TEMPLATE_ARGS> >::type invoker;
    };
#if GL_FUNC_PARAMS_NUM > 0
    template <typename F, typename R GL_FUNC_COMMA GL_FUNC_TEMPLATE_PARAMS>
    struct GL_FUNC_GET_FUNCTION_INVOKER<member_function_ptr_tag, F, R GL_FUNC_COMMA GL_FUNC_TEMPLATE_ARGS> {
        typedef typename if_c<is_void<R>::value,
                GL_FUNC_VOID_MEMBER_FUNCTION_INVOKER<F GL_FUNC_COMMA GL_FUNC_TEMPLATE_ARGS>,
                GL_FUNC_MEMBER_FUNCTION_INVOKER<F, R GL_FUNC_COMMA GL_FUNC_TEMPLATE_ARGS> >::type invoker;
    };
#endif

} /* detail */


template <typename R GL_FUNC_COMMA GL_FUNC_TEMPLATE_PARAMS>
class GL_FUNC_NAME {

public:

    typedef R result_type;
    typedef GL_FUNC_NAME self_type;
    typedef result_type (*invoker_type)(const functor & GL_FUNC_COMMA GL_FUNC_TEMPLATE_ARGS);

    /* do not use (const _F &f) to enable decay */
    template <typename F>
    GL_FUNC_NAME(F f) : m_invoker(0) {
        assign(f);
    }

    /* empty function */
    GL_FUNC_NAME(int zero) : m_invoker(0) {
        assert(zero == 0);
    }

    /* destructor */
    ~GL_FUNC_NAME() {
        clear();
    }

    bool empty() const {
        return m_invoker == 0;
    }

    result_type operator()(GL_FUNC_PARAMS) const {
        if (empty()) {
           throw std::runtime_error("bad function call");
        }
        return m_invoker(m_func GL_FUNC_COMMA GL_FUNC_ARGS);
    }

private:

    void clear() {
        m_func.clear();
        m_invoker = 0;
    }

    template <typename F>
    void assign(F &f) {
        typedef typename get_function_tag<F>::type tag;
        typedef typename detail::GL_FUNC_GET_FUNCTION_INVOKER<tag, F, R GL_FUNC_COMMA GL_FUNC_TEMPLATE_ARGS>::invoker invoker;
        m_func.assign(f, tag());
        m_invoker = &invoker::invoke; /* compile error here if type mismatch */
    }

    functor m_func;
    invoker_type m_invoker;

};


template <typename R GL_FUNC_COMMA GL_FUNC_TEMPLATE_PARAMS>
class function<R (GL_FUNC_TEMPLATE_ARGS)> : public GL_FUNC_NAME<R GL_FUNC_COMMA GL_FUNC_TEMPLATE_ARGS> {

public:

    typedef GL_FUNC_NAME<R GL_FUNC_COMMA GL_FUNC_TEMPLATE_ARGS> base_type;

    template <typename F>
    function(F f) : base_type(f) { }

    /* we do not need virtual destructors here, since no resource is managed here presently. */
};


} /* gl */


/* clear definitions */
#undef GL_FUNC_NAME
#undef GL_FUNC_VOID_FUNCTION_INVOKER
#undef GL_FUNC_FUNCTION_INVOKER
#undef GL_FUNC_VOID_FUNCTION_OBJ_INVOKER
#undef GL_FUNC_FUNCTION_OBJ_INVOKER
#undef GL_FUNC_VOID_FUNCTION_REF_INVOKER
#undef GL_FUNC_FUNCTION_REF_INVOKER
#undef GL_FUNC_VOID_MEMBER_INVOKER
#undef GL_FUNC_MEMBER_INVOKER
#undef GL_FUNC_GET_FUNCTION_INVOKER
