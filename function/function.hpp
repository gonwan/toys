#ifndef _GL_FUNCTION_HPP
#define _GL_FUNCTION_HPP


#define _GL_PP_FUNCTION


#include <iostream>
#include <typeinfo>


namespace gl {


template <typename Sig>
class function;


} /* gl */


#ifndef _GL_PP_FUNCTION
#include "_function0.hpp"
#include "_function1.hpp"
#include "_function2.hpp"
#else

/* 0 */
#define GL_FUNC_COMMA
#define GL_FUNC_PARAMS_NUM          0
#define GL_FUNC_TEMPLATE_PARAMS
#define GL_FUNC_PARAMS
#define GL_FUNC_TEMPLATE_ARGS
#define GL_FUNC_ARGS
#include "function_template.hpp"
#undef GL_FUNC_COMMA
#undef GL_FUNC_PARAMS_NUM
#undef GL_FUNC_TEMPLATE_PARAMS
#undef GL_FUNC_PARAMS
#undef GL_FUNC_TEMPLATE_ARGS
#undef GL_FUNC_ARGS
/* 1 */
#define GL_FUNC_COMMA               ,
#define GL_FUNC_PARAMS_NUM          1
#define GL_FUNC_TEMPLATE_PARAMS     typename A1
#define GL_FUNC_PARAMS              A1 a1
#define GL_FUNC_TEMPLATE_ARGS       A1
#define GL_FUNC_ARGS                a1
#include "function_template.hpp"
#undef GL_FUNC_COMMA
#undef GL_FUNC_PARAMS_NUM
#undef GL_FUNC_TEMPLATE_PARAMS
#undef GL_FUNC_PARAMS
#undef GL_FUNC_TEMPLATE_ARGS
#undef GL_FUNC_ARGS
/* 2 */
#define GL_FUNC_COMMA               ,
#define GL_FUNC_PARAMS_NUM          2
#define GL_FUNC_TEMPLATE_PARAMS     typename A1, typename A2
#define GL_FUNC_PARAMS              A1 a1, A2 a2
#define GL_FUNC_TEMPLATE_ARGS       A1, A2
#define GL_FUNC_ARGS                a1, a2
#include "function_template.hpp"
#undef GL_FUNC_COMMA
#undef GL_FUNC_PARAMS_NUM
#undef GL_FUNC_TEMPLATE_PARAMS
#undef GL_FUNC_PARAMS
#undef GL_FUNC_TEMPLATE_ARGS
#undef GL_FUNC_ARGS
/* 3 */
#define GL_FUNC_COMMA               ,
#define GL_FUNC_PARAMS_NUM          3
#define GL_FUNC_TEMPLATE_PARAMS     typename A1, typename A2, typename A3
#define GL_FUNC_PARAMS              A1 a1, A2 a2, A3 a3
#define GL_FUNC_TEMPLATE_ARGS       A1, A2, A3
#define GL_FUNC_ARGS                a1, a2, a3
#include "function_template.hpp"
#undef GL_FUNC_COMMA
#undef GL_FUNC_PARAMS_NUM
#undef GL_FUNC_TEMPLATE_PARAMS
#undef GL_FUNC_PARAMS
#undef GL_FUNC_TEMPLATE_ARGS
#undef GL_FUNC_ARGS
/* 4 */
#define GL_FUNC_COMMA               ,
#define GL_FUNC_PARAMS_NUM          4
#define GL_FUNC_TEMPLATE_PARAMS     typename A1, typename A2, typename A3, typename A4
#define GL_FUNC_PARAMS              A1 a1, A2 a2, A3 a3, A4 a4
#define GL_FUNC_TEMPLATE_ARGS       A1, A2, A3, A4
#define GL_FUNC_ARGS                a1, a2, a3, a4
#include "function_template.hpp"
#undef GL_FUNC_COMMA
#undef GL_FUNC_PARAMS_NUM
#undef GL_FUNC_TEMPLATE_PARAMS
#undef GL_FUNC_PARAMS
#undef GL_FUNC_TEMPLATE_ARGS
#undef GL_FUNC_ARGS
/* 5 */
#define GL_FUNC_COMMA               ,
#define GL_FUNC_PARAMS_NUM          5
#define GL_FUNC_TEMPLATE_PARAMS     typename A1, typename A2, typename A3, typename A4, typename A5
#define GL_FUNC_PARAMS              A1 a1, A2 a2, A3 a3, A4 a4, A5 a5
#define GL_FUNC_TEMPLATE_ARGS       A1, A2, A3, A4, A5
#define GL_FUNC_ARGS                a1, a2, a3, a4, a5
#include "function_template.hpp"
#undef GL_FUNC_COMMA
#undef GL_FUNC_PARAMS_NUM
#undef GL_FUNC_TEMPLATE_PARAMS
#undef GL_FUNC_PARAMS
#undef GL_FUNC_TEMPLATE_ARGS
#undef GL_FUNC_ARGS
/* 6 */
#define GL_FUNC_COMMA               ,
#define GL_FUNC_PARAMS_NUM          6
#define GL_FUNC_TEMPLATE_PARAMS     typename A1, typename A2, typename A3, typename A4, typename A5, typename A6
#define GL_FUNC_PARAMS              A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6
#define GL_FUNC_TEMPLATE_ARGS       A1, A2, A3, A4, A5, A6
#define GL_FUNC_ARGS                a1, a2, a3, a4, a5, a6
#include "function_template.hpp"
#undef GL_FUNC_COMMA
#undef GL_FUNC_PARAMS_NUM
#undef GL_FUNC_TEMPLATE_PARAMS
#undef GL_FUNC_PARAMS
#undef GL_FUNC_TEMPLATE_ARGS
#undef GL_FUNC_ARGS
/* 7 */
#define GL_FUNC_COMMA               ,
#define GL_FUNC_PARAMS_NUM          7
#define GL_FUNC_TEMPLATE_PARAMS     typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7
#define GL_FUNC_PARAMS              A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7
#define GL_FUNC_TEMPLATE_ARGS       A1, A2, A3, A4, A5, A6, A7
#define GL_FUNC_ARGS                a1, a2, a3, a4, a5, a6, a7
#include "function_template.hpp"
#undef GL_FUNC_COMMA
#undef GL_FUNC_PARAMS_NUM
#undef GL_FUNC_TEMPLATE_PARAMS
#undef GL_FUNC_PARAMS
#undef GL_FUNC_TEMPLATE_ARGS
#undef GL_FUNC_ARGS
/* 8 */
#define GL_FUNC_COMMA               ,
#define GL_FUNC_PARAMS_NUM          8
#define GL_FUNC_TEMPLATE_PARAMS     typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8
#define GL_FUNC_PARAMS              A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8
#define GL_FUNC_TEMPLATE_ARGS       A1, A2, A3, A4, A5, A6, A7, A8
#define GL_FUNC_ARGS                a1, a2, a3, a4, a5, a6, a7, a8
#include "function_template.hpp"
#undef GL_FUNC_COMMA
#undef GL_FUNC_PARAMS_NUM
#undef GL_FUNC_TEMPLATE_PARAMS
#undef GL_FUNC_PARAMS
#undef GL_FUNC_TEMPLATE_ARGS
#undef GL_FUNC_ARGS
/* 9 */
#define GL_FUNC_COMMA               ,
#define GL_FUNC_PARAMS_NUM          9
#define GL_FUNC_TEMPLATE_PARAMS     typename A1, typename A2, typename A3, typename A4, typename A5, typename A6, typename A7, typename A8, typename A9
#define GL_FUNC_PARAMS              A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8, A9 a9
#define GL_FUNC_TEMPLATE_ARGS       A1, A2, A3, A4, A5, A6, A7, A8, A9
#define GL_FUNC_ARGS                a1, a2, a3, a4, a5, a6, a7, a8, a9
#include "function_template.hpp"
#undef GL_FUNC_COMMA
#undef GL_FUNC_PARAMS_NUM
#undef GL_FUNC_TEMPLATE_PARAMS
#undef GL_FUNC_PARAMS
#undef GL_FUNC_TEMPLATE_ARGS
#undef GL_FUNC_ARGS

#endif


#endif
