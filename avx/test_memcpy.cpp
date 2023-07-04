/* g++ -std=c++14 -O2 -mavx test_memcpy.cpp -o test_memcpy */
/* cl -nologo -arch:AVX -O2 test_memcpy.cpp */
#include <immintrin.h>
#include <stdlib.h>
#include <string.h>
#include <chrono>
#include <iostream>
#include <thread>

using namespace std;
using namespace std::chrono;
using namespace std::chrono_literals;

void *memcpy_fast512(void *dst, const void *src, size_t n) {
    unsigned char *d = (unsigned char *) dst;
    const unsigned char *s = (const unsigned char *) src;
#if 0 /* -mavx512f */
    while (n >= 64) {
        _mm512_storeu_si512(d, _mm512_loadu_si512(s));
        s += 64;
        d += 64;
        n -= 64;
    }
#endif
    memcpy(d, s, n);
    return dst;
}

void *memcpy_fast(void *dst, const void *src, size_t n) {
    unsigned char *d = (unsigned char *) dst;
    const unsigned char *s = (const unsigned char *) src;
#if 1
    /* loop unrolling */
    while (n >= 512) { /* 512 bytes */
        _mm256_storeu_si256((__m256i*) d, _mm256_loadu_si256((__m256i const*) s));
        _mm256_storeu_si256((__m256i*) (d + 32), _mm256_loadu_si256((__m256i const*) (s + 32)));
        _mm256_storeu_si256((__m256i*) (d + 64), _mm256_loadu_si256((__m256i const*) (s + 64)));
        _mm256_storeu_si256((__m256i*) (d + 96), _mm256_loadu_si256((__m256i const*) (s + 96)));
        _mm256_storeu_si256((__m256i*) (d + 128), _mm256_loadu_si256((__m256i const*) (s + 128)));
        _mm256_storeu_si256((__m256i*) (d + 160), _mm256_loadu_si256((__m256i const*) (s + 160)));
        _mm256_storeu_si256((__m256i*) (d + 192), _mm256_loadu_si256((__m256i const*) (s + 192)));
        _mm256_storeu_si256((__m256i*) (d + 224), _mm256_loadu_si256((__m256i const*) (s + 224)));
        _mm256_storeu_si256((__m256i*) (d + 256), _mm256_loadu_si256((__m256i const*) (s + 256)));
        _mm256_storeu_si256((__m256i*) (d + 288), _mm256_loadu_si256((__m256i const*) (s + 288)));
        _mm256_storeu_si256((__m256i*) (d + 320), _mm256_loadu_si256((__m256i const*) (s + 320)));
        _mm256_storeu_si256((__m256i*) (d + 352), _mm256_loadu_si256((__m256i const*) (s + 352)));
        _mm256_storeu_si256((__m256i*) (d + 384), _mm256_loadu_si256((__m256i const*) (s + 384)));
        _mm256_storeu_si256((__m256i*) (d + 416), _mm256_loadu_si256((__m256i const*) (s + 416)));
        _mm256_storeu_si256((__m256i*) (d + 448), _mm256_loadu_si256((__m256i const*) (s + 448)));
        _mm256_storeu_si256((__m256i*) (d + 480), _mm256_loadu_si256((__m256i const*) (s + 480)));
        s += 512;
        d += 512;
        n -= 512;
    }
    size_t r = (n >> 8); /* n/256 */
    if (r >= 8) { /* 256 bytes */
        _mm256_storeu_si256((__m256i*) d, _mm256_loadu_si256((__m256i const*) s));
        _mm256_storeu_si256((__m256i*) (d + 32), _mm256_loadu_si256((__m256i const*) (s + 32)));
        _mm256_storeu_si256((__m256i*) (d + 64), _mm256_loadu_si256((__m256i const*) (s + 64)));
        _mm256_storeu_si256((__m256i*) (d + 96), _mm256_loadu_si256((__m256i const*) (s + 96)));
        _mm256_storeu_si256((__m256i*) (d + 128), _mm256_loadu_si256((__m256i const*) (s + 128)));
        _mm256_storeu_si256((__m256i*) (d + 160), _mm256_loadu_si256((__m256i const*) (s + 160)));
        _mm256_storeu_si256((__m256i*) (d + 192), _mm256_loadu_si256((__m256i const*) (s + 192)));
        _mm256_storeu_si256((__m256i*) (d + 224), _mm256_loadu_si256((__m256i const*) (s + 224)));
        s += 256;
        d += 256;
        n -= 256;
        r -= 8;
    }
    if (r >= 4) { /* 128 bytes */
        _mm256_storeu_si256((__m256i*) d, _mm256_loadu_si256((__m256i const*) s));
        _mm256_storeu_si256((__m256i*) (d + 32), _mm256_loadu_si256((__m256i const*) (s + 32)));
        _mm256_storeu_si256((__m256i*) (d + 64), _mm256_loadu_si256((__m256i const*) (s + 64)));
        _mm256_storeu_si256((__m256i*) (d + 96), _mm256_loadu_si256((__m256i const*) (s + 96)));
        s += 128;
        d += 128;
        n -= 128;
        r -= 4;
    }
    if (r >= 2) { /* 64 bytes */
        _mm256_storeu_si256((__m256i*) d, _mm256_loadu_si256((__m256i const*) s));
        _mm256_storeu_si256((__m256i*) (d + 32), _mm256_loadu_si256((__m256i const*) (s + 32)));
        s += 64;
        d += 64;
        n -= 64;
        r -= 2;
    }
#else
    while (n >= 32) {
        _mm256_storeu_si256((__m256i *) d, _mm256_loadu_si256((__m256i const *) s));
        s += 32;
        d += 32;
        n -= 32;
    }
#endif
    memcpy(d, s, n);
    return dst;
}

void benchmark_random(int maxsize, int times) {
    static char A[150 * 1024 * 1024 + 2];
    static char B[150 * 1024 * 1024 + 2];
    static int random_offsets[0x10000];
    static int random_sizes[0x8000];
    unsigned int i, p1, p2;
    if (maxsize > 128 * 1024 * 1024) {
        cout << "maxsize is 128M" << endl;
        return;
    }
    for (i = 0; i < 0x10000; i++) {
        random_offsets[i] = rand() % (1 * 1024 * 1024);
    }
    for (i = 0; i < 0x8000; i++) {
        random_sizes[i] = (int) (rand() % maxsize * 0.5 + maxsize * 0.5);
    }
    this_thread::sleep_for(1000ms);
    auto t0 = high_resolution_clock::now();
    for (p1 = 0, p2 = 0, i = 0; i < times; i++) {
        int offset1 = random_offsets[(p1++) & 0xffff];
        int offset2 = random_offsets[(p1++) & 0xffff];
        int size = random_sizes[(p2++) & 0x7fff];
        /* centos7 has glibc 2.17, that has no avx2 ifunc, but sse2/sse3 ones. */
        memcpy(A + offset1, B + offset2, size);
    }
    auto d1 = duration_cast<std::chrono::milliseconds>(high_resolution_clock::now() - t0);
    this_thread::sleep_for(1000ms);
    t0 = high_resolution_clock::now();
    for (p1 = 0, p2 = 0, i = 0; i < times; i++) {
        int offset1 = random_offsets[(p1++) & 0xffff];
        int offset2 = random_offsets[(p1++) & 0xffff];
        int size = random_sizes[(p2++) & 0x7fff];
        memcpy_fast(A + offset1, B + offset2, size);
    }
    auto d2 = duration_cast<std::chrono::milliseconds>(high_resolution_clock::now() - t0);
    cout << "benchmark random access: avg size=" << maxsize << endl;
    cout << "memcpy=" << d1.count() << "ms memcpy_fast=" << d2.count() << "ms" << endl;
}

void test_equal(size_t n) {
    char *A = (char *) malloc(n+1);
    char *B = (char *) malloc(n+1);
    memset(A, 0, n+1);
    memset(B, 0, n+1);
    for (size_t i = 0; i < n; i++) {
        B[i] = 'A' + (rand() % 26);
    }
    memcpy_fast(A, B, n);
    int b = memcmp(A, B, n);
    cout << "test equal: size=" << n << " equal=" << b << endl;
    free(B);
    free(A);
}

int main() {
    test_equal(32);
    test_equal(1024);
    test_equal(4 * 1024 * 1 + 100);
    test_equal(1024 * 1024 * 4 + 100);
    benchmark_random(32, 0x1000000);
    benchmark_random(64, 0x1000000);
    benchmark_random(512, 0x800000);
    benchmark_random(1024, 0x400000);
    benchmark_random(4096, 0x100000);
    benchmark_random(8192, 0x80000);
    benchmark_random(1024 * 1024 * 1, 0x800); /* 2G */
    benchmark_random(1024 * 1024 * 2, 0x800); /* 4G */
    benchmark_random(1024 * 1024 * 4, 0x800); /* 8G */
    benchmark_random(1024 * 1024 * 8, 0x800); /* 16G */
    benchmark_random(1024 * 1024 * 16, 0x400); /* 16G */
    benchmark_random(1024 * 1024 * 32, 0x400); /* 32G */
    benchmark_random(1024 * 1024 * 64, 0x200); /* 32G */
    benchmark_random(1024 * 1024 * 128, 0x100); /* 32G */
    return 0;
}
