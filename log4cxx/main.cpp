#include <iostream>
#include <log4cxx/logger.h>
#include <log4cxx/xml/domconfigurator.h>
#include <fmt/format.h>
#include <boost/stacktrace.hpp>

using namespace std;
using namespace log4cxx;
using namespace log4cxx::xml;

LoggerPtr logger(Logger::getRootLogger());

class A {
public:
    void do1() {
        cout << boost::stacktrace::stacktrace() << endl;
    }
};

class B {
public:
    void do2() {
        A a;
        a.do1();
    }
};

void a() {
    B b;
    b.do2();
}

void b() {
    a();
}

int main() {
    DOMConfigurator::configure("log4cxx.xml");
    LOG4CXX_INFO_FMT(logger, "Numbers can be formatted with a format string {:.1f} and as hex: {:x}", 22.456, 123);
    b();
    return 0;
}
