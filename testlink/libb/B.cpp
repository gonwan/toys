#include "B.h"
#include <iostream>
using namespace std;


void B::print(const string &str) {
    cout << "print from b: " << str << endl;
    cout << "version: " << __cplusplus
        << ", _GLIBCXX_USE_DUAL_ABI: " << _GLIBCXX_USE_DUAL_ABI
        << ", _GLIBCXX_USE_CXX11_ABI: " << _GLIBCXX_USE_CXX11_ABI << endl;
}
