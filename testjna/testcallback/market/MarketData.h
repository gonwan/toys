#ifndef TESTCALLBACK_MARKETDATA_H
#define TESTCALLBACK_MARKETDATA_H
#include "MarketDataIf.h"
#include "link.h"


class MARKETDATA_API MarketData {
public:
    MarketData() : m_marketDataIf(nullptr), m_stop(false) { }
    void AddCallback(const MarketDataIf *marketDataIf);
    void Run();
    void Stop();
private:
    MarketDataIf *m_marketDataIf;
    volatile bool m_stop;
};


#endif //TESTCALLBACK_MARKETDATA_H
