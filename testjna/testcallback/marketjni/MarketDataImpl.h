#ifndef TESTCALLBACK_MARKETDATAIMPL_H
#define TESTCALLBACK_MARKETDATAIMPL_H
#include "../market/MarketDataIf.h"
#include "MarketDataC.h"


class MarketDataImpl : public MarketDataIf {
public:
    MarketDataImpl(java_callbacks cb) : m_cb(cb) { }
    bool OnTest();
    bool OnPerf(const data_type1* pData, int intSize);
    bool OnUpdate(const data_type1 *pData, int intSize);
    bool OnUpdate(const data_type2 *pData, int intSize);
private:
    java_callbacks m_cb;
};


#endif //TESTCALLBACK_MARKETDATAIMPL_H
