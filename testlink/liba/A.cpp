#include "A.h"
#include <iostream>
using namespace std;


void A::print(const string &str) {
    cout << "print from a: " << str << endl;
    cout << "version: " << __cplusplus << endl;
}
