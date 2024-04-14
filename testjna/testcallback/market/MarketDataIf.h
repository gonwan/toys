#ifndef TESTCALLBACK_MARKETDATAIF_H
#define TESTCALLBACK_MARKETDATAIF_H
#include "link.h"


#pragma pack(push, 1)


class MARKETDATA_API data_type1 {
public:
    int seq;
    int a;
    double b;
    char c[12];
};

class MARKETDATA_API data_type2e {
public:
    int aaa;
};

class MARKETDATA_API data_type2 {
public:
    int seq;
    char aa;
    short bb;
    long long cc;
    /* embedded type */
    data_type2e *dd;
    int ddlen;
    /* translib also have bigint type */
};


#pragma pack(pop)


class MARKETDATA_API MarketDataIf {
public:
    virtual bool OnTest() = 0;
    virtual bool OnPerf(const data_type1* pData, int intSize) = 0;
    virtual bool OnUpdate(const data_type1 *pData, int intSize) = 0;
    virtual bool OnUpdate(const data_type2 *pData, int intSize) = 0;
};


#endif //TESTCALLBACK_MARKETDATAIF_H
