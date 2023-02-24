#ifndef TESTCALLBACK_MARKETDATAC_H
#define TESTCALLBACK_MARKETDATAC_H
#include "MarketDataIfC.h"
#include "link.h"


#ifdef __cplusplus
extern "C" {
#endif


MARKETDATAJNI_API void print(const char *s);

/* jni callbacks */
typedef struct _java_callbacks {
    int (*on_test)();
    int (*on_perf)(const data_type1_t* pData, int intSize);
    int (*on_update_type1)(const data_type1_t *pData, int intSize);
    int (*on_update_type2)(const data_type2_t *pData, int intSize);
} java_callbacks;

/* c wrappers */
typedef void *market_data_handler;
typedef void *market_data_if_handler;

MARKETDATAJNI_API void test(const data_type1_t *pData1, int intSize1, const data_type2_t *pData2, int intSize2);
MARKETDATAJNI_API market_data_handler market_data_create();
MARKETDATAJNI_API void market_data_destroy(market_data_handler mdh);
MARKETDATAJNI_API void market_data_add_callback(market_data_handler mdh, market_data_if_handler mdih);
MARKETDATAJNI_API void market_data_run(market_data_handler mdh);
MARKETDATAJNI_API void market_data_stop(market_data_handler mdh);
MARKETDATAJNI_API market_data_if_handler market_data_if_create(java_callbacks cb);
MARKETDATAJNI_API void market_data_if_destroy(market_data_if_handler mdih);


#ifdef __cplusplus
}
#endif
#endif //TESTCALLBACK_MARKETDATAC_H
