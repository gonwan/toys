#include <string.h>
#include <string>
#include <thread>
#include "MarketData.h"
using namespace std;


void MarketData::AddCallback(const MarketDataIf *marketDataIf) {
    m_marketDataIf = const_cast<MarketDataIf *>(marketDataIf);
}

void MarketData::Run() {
    int seq = 0;
    /* dt1 */
    data_type1 dt1[2] = {
            { .seq= 0, .a = 1, .b = 1.1 },
            { .seq= 0, .a = 2, .b = 2.2 }
    };
    strcpy(dt1[0].c, "111");
    strcpy(dt1[1].c, "222");
    /* dt2 */
    data_type2 dt2[2] = {
            { .seq= 0, .aa = '1', .bb = 11, .cc = 111111111111L },
            { .seq= 0, .aa = '2', .bb = 22, .cc = 222222222222L }
    };
    data_type2e dt2e1[2] = { 1, 2 };
    data_type2e dt2e2[4] = { 1, 2, 3, 4 };
    dt2[0].dd = dt2e1;
    dt2[0].ddlen = 2;
    dt2[1].dd = dt2e2;
    dt2[1].ddlen = 4;
    /* perf */
    size_t sz = 10000 * 100; /* 1 million */
    data_type1 *dt_perf = new data_type1[sz];
    for (size_t i = 0; i < sz; i++) {
        dt_perf[i].seq = i;
        dt_perf[i].a = i;
        dt_perf[i].b = i;
        strcpy(dt_perf[i].c, std::to_string(i).c_str());
    }
    while (!m_stop) {
        seq++;
        dt1[0].seq = seq;
        dt1[1].seq = seq;
        dt2[0].seq = seq;
        dt2[1].seq = seq;
        m_marketDataIf->OnTest();
        m_marketDataIf->OnPerf(dt_perf, sz);
        m_marketDataIf->OnUpdate(dt1, 2);
        m_marketDataIf->OnUpdate(dt2, 2);
        this_thread::sleep_for(5s);
    }
    delete[] dt_perf;
}

void MarketData::Stop() {
    m_stop = true;
}
