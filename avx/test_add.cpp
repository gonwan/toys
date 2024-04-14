/* g++ -std=c++14 -O2 -mavx2 test_add.cpp -o test_add */
/* cl -nologo -arch:AVX -O2 test_add.cpp */
#include <immintrin.h>
#include <stdlib.h>
#include <string.h>
#include <chrono>
#include <iostream>
#include <thread>

using namespace std;
using namespace std::chrono;
using namespace std::chrono_literals;

/* seems has no difference */
void benchmark_add(size_t size) {
    size = size & (~0x3);
    int *A = new int[size];
    int *B = new int[size];
    int *C1 = new int[size]();
    int *C2 = new int[size]();
    for (int i = 0; i < size; i++) {
        A[i] = rand() % (1 * 1024 * 1024);
        B[i] = rand() % (2 * 1024 * 1024);
    }
    this_thread::sleep_for(1000ms);
    auto t0 = high_resolution_clock::now();
    for (int i = 0; i < size; i++) {
        C1[i] = A[i] + B[i];
    }
    auto d1 = duration_cast<std::chrono::milliseconds>(high_resolution_clock::now() - t0);
    this_thread::sleep_for(1000ms);
    t0 = high_resolution_clock::now();
    for (int i = 0; i < size; i+=8) {
        __m256i a = _mm256_loadu_si256((__m256i const*)(A+i));
        __m256i b = _mm256_loadu_si256((__m256i const*)(B+i));
        __m256i r = _mm256_add_epi32(a, b);
        _mm256_storeu_si256((__m256i*)(C2+i), r);
    }
    auto d2 = duration_cast<std::chrono::milliseconds>(high_resolution_clock::now() - t0);
    int b = memcmp(C1, C2, sizeof(int) * size);
    cout << "benchmark add: size=" << size << " equal=" << b << endl;
    cout << "add=" << d1.count() << "ms add_fast=" << d2.count() << "ms" << endl;
}

int main() {
    benchmark_add(1024 * 1024);
    benchmark_add(1024 * 1024 * 16);
    benchmark_add(1024 * 1024 * 64);
    benchmark_add(1024 * 1024 * 256);
    return 0;
}
