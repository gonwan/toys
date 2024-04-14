#include <stdio.h>
#include "MarketDataC.h"
#include "MarketDataImpl.h"
#include "../market/MarketData.h"


void print(const char *s) {
    printf("c: %s", s);
    fflush(stdout);
}

void test(const data_type1_t *pData1, int intSize1, const data_type2_t *pData2, int intSize2) {
    printf("data_type1: seq=%d, c1=%s, c2=%s\n", pData1[0].seq, pData1[0].c, pData1[1].c);
    printf("data_type2: seq=%d, cc1=%lld, cc1_aaa1=%d, cc2=%lld, cc2_aaa2=%d\n",
        pData2[0].seq, pData2[0].cc, pData2[0].dd[0].aaa, pData2[1].cc, pData2[1].dd[1].aaa);
    fflush(stdout);
}

market_data_handler market_data_create() {
    return new MarketData;
}

void market_data_destroy(market_data_handler mdh) {
    MarketData *md = (MarketData *) mdh;
    delete md;
}

void market_data_add_callback(market_data_handler mdh, market_data_if_handler mdih) {
    MarketData *md = (MarketData *) mdh;
    MarketDataIf *mdi = (MarketDataIf *) mdih;
    md->AddCallback(mdi);
}

void market_data_run(market_data_handler mdh) {
    MarketData *md = (MarketData *) mdh;
    md->Run();
}

void market_data_stop(market_data_handler mdh) {
    MarketData *md = (MarketData *) mdh;
    md->Stop();
}

market_data_if_handler market_data_if_create(java_callbacks cb) {
    return new MarketDataImpl(cb);
}

void market_data_if_destroy(market_data_if_handler mdih) {
    MarketDataImpl *mdi = (MarketDataImpl *) mdih;
    delete mdi;
}
