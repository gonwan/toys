#ifndef TESTCALLBACK_MARKETJNI_LINK_H
#define TESTCALLBACK_MARKETJNI_LINK_H


#ifdef _WIN32
#ifndef MARKETDATAJNI_EXPORTS
#define MARKETDATAJNI_API __declspec(dllimport)
#else
#define MARKETDATAJNI_API __declspec(dllexport)
#endif
#else
#define MARKETDATAJNI_API
#endif


#endif
