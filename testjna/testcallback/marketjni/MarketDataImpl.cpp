#include <stdio.h>
#include <chrono>
#include "MarketDataImpl.h"
#include "MarketDataC.h"
#include "MarketDataIfC.h"


bool MarketDataImpl::OnTest() {
    static int i = 0;
    printf("c++: %d\n", i);
    fflush(stdout);
    if (m_cb.on_test) {
        m_cb.on_test();
    }
    return true;
}

bool MarketDataImpl::OnPerf(const data_type1* pData, int intSize) {
    printf("c++: size=%d\n", intSize);
    fflush(stdout);
    auto t0 = std::chrono::high_resolution_clock::now();
    if (m_cb.on_perf) {
        m_cb.on_perf((data_type1_t*) pData, intSize);
    }
    auto ns = std::chrono::duration_cast<std::chrono::nanoseconds>(std::chrono::high_resolution_clock::now() - t0);
    printf("c++: %lld ns elapsed\n", (long long) ns.count());
    fflush(stdout);
    return true;
}

bool MarketDataImpl::OnUpdate(const data_type1 *pData, int intSize) {
    printf("c++: seq=%d, a=%d, b=%f, c=%s\n", pData[0].seq, pData[0].a, pData[0].b, pData[0].c);
    fflush(stdout);
    if (m_cb.on_update_type1) {
        m_cb.on_update_type1((data_type1_t *) pData, intSize);
    }
    return true;
}

bool MarketDataImpl::OnUpdate(const data_type2 *pData, int intSize) {
    printf("c++: seq=%d, aa=%c, bb=%d, cc=%lld, dd=%p, ddlen=%d\n", pData[0].seq, pData[0].aa, pData[0].bb, pData[0].cc, pData[0].dd, pData[0].ddlen);
    fflush(stdout);
    if (m_cb.on_update_type2) {
        m_cb.on_update_type2((data_type2_t *) pData, intSize);
    }
    return true;
}
