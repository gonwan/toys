#include "liba/A.h"
#include "libb/B.h"
#include <string>
#include <iostream>
using namespace std;


/**
 * 1. CentOS6/CentOS7 devtoolset-[7-11] configures with: --with-default-libstdcxx-abi=gcc4-compatible, actually there is
 * no chance to use the new C++11 ABI.
 * 2. Debian also switched to use new ABI starting Debian9, see: https://wiki.debian.org/GCC5
 */
int main() {
    string s1 = "hahaha";
    string s2 = "heyhey";
    A a;
    B b;
    a.print(s1);
    b.print(s2);
    cout << "print from main:" << endl;
    cout << "version: " << __cplusplus
         << ", _GLIBCXX_USE_DUAL_ABI: " << _GLIBCXX_USE_DUAL_ABI
         << ", _GLIBCXX_USE_CXX11_ABI: " << _GLIBCXX_USE_CXX11_ABI << endl;
    return 0;
}
