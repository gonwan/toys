#include <stdio.h>
#include "marketjni/MarketDataC.h"


int on_update_type1(const data_type1_t *pData, int intSize) {
    printf("callback: seq=%d, a=%d, b=%f, c=%s\n", pData[0].seq, pData[0].a, pData[0].b, pData[0].c);
    fflush(stdout);
    return 0;
}

int on_update_type2(const data_type2_t *pData, int intSize) {
    printf("callback: seq=%d, aa=%c, bb=%d, cc=%lld, dd=%p, ddlen=%d\n", pData[0].seq, pData[0].aa, pData[0].bb, pData[0].cc, pData[0].dd, pData[0].ddlen);
    fflush(stdout);
    return 0;
}

int main() {
    java_callbacks cb = { 0 };
    cb.on_update_type1 = on_update_type1;
    cb.on_update_type2 = on_update_type2;
    market_data_handler mdh = market_data_create();
    market_data_if_handler mdih = market_data_if_create(cb);
    market_data_add_callback(mdh, mdih);
    market_data_run(mdh);
    return 0;
}
