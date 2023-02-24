#ifndef TESTCALLBACK_MARKET_LINK_H
#define TESTCALLBACK_MARKET_LINK_H


#ifdef _WIN32
#ifndef MARKETDATA_EXPORTS
#define MARKETDATA_API __declspec(dllimport)
#else
#define MARKETDATA_API __declspec(dllexport)
#endif
#else
#define MARKETDATA_API
#endif


#endif
