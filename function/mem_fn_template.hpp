namespace gl {


namespace detail {


template <class R, class T GL_MEMFN_COMMA GL_MEMFN_TEMPLATE_PARAMS>
class GL_MEMFN_NAME {
private:
    typedef R (T::*F)(GL_MEMFN_TEMPLATE_ARGS) GL_CONST_MARK;
    F f_;
public:
    typedef R result_type;
    explicit GL_MEMFN_NAME(F f): f_(f) { } /* compile error here if type mismatch */
    template <typename U>
    R operator()(U *u GL_MEMFN_COMMA GL_MEMFN_PARAMS) const {
        return (u->*f_)(GL_MEMFN_ARGS);
    }
    template <typename U>
    R operator()(U &u GL_MEMFN_COMMA GL_MEMFN_PARAMS) const {
        return (u.*f_)(GL_MEMFN_ARGS);
    }
};


} /* detail */


template <class R, class T GL_MEMFN_COMMA GL_MEMFN_TEMPLATE_PARAMS>
detail::GL_MEMFN_NAME<R, T GL_MEMFN_COMMA GL_MEMFN_TEMPLATE_ARGS> mem_fn(R (T::*f)(GL_MEMFN_TEMPLATE_ARGS) GL_CONST_MARK)
{
    return detail::GL_MEMFN_NAME<R, T GL_MEMFN_COMMA GL_MEMFN_TEMPLATE_ARGS>(f);
}


} /* gl */
