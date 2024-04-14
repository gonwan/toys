#ifndef TESTCALLBACK_MARKETDATAIFC_H
#define TESTCALLBACK_MARKETDATAIFC_H
#include <inttypes.h>


#ifdef __cplusplus
extern "C" {
#endif


#pragma pack(push, 1)


typedef struct _data_type1_t {
    int seq;
    int a;
    double b;
    char c[12];
} data_type1_t;

typedef struct _data_type2e_t {
    int aaa;
} data_type2e_t;

typedef struct _data_type2_t {
    int seq;
    char aa;
    short bb;
    long long cc;
    data_type2e_t *dd;
    int ddlen;
} data_type2_t;


#pragma pack(pop)


#ifdef __cplusplus
}
#endif
#endif //TESTCALLBACK_MARKETDATAIFC_H
