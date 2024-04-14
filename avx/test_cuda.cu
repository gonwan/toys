/* nvcc -O2 test_cuda.cu -o test_cuda */
/*
benchmark sma: size=1048576 sample=5 equal=0
sma_cpu=8ms sma_gpu=64ms
benchmark sma: size=1048576 sample=5 equal=0
sma_cpu=8ms sma_gpu=6ms
benchmark sma: size=33554432 sample=5 equal=0
sma_cpu=115ms sma_gpu=49ms
benchmark sma: size=1073741824 sample=5 equal=0
sma_cpu=1575ms sma_gpu=862ms
benchmark sma: size=1048576 sample=30 equal=0
sma_cpu=6ms sma_gpu=8ms
benchmark sma: size=33554432 sample=30 equal=0
sma_cpu=54ms sma_gpu=33ms
benchmark sma: size=1073741824 sample=30 equal=0
sma_cpu=1567ms sma_gpu=880ms
 */
#include <chrono>
#include <iostream>
#include <thread>
#include <cuda_runtime.h>
using namespace std;
using namespace std::chrono;
using namespace std::chrono_literals;

__global__ void sma_gpu_kernel(const int *input, int *output, int size, int sample)
{
    int tid = blockIdx.x * blockDim.x + threadIdx.x;
    if (tid < size) {
        int sum = 0;
        int count = 0;
        for (int i = tid; i >= 0 && i > tid - sample; i--) {
            sum += input[i];
            count++;
        }
        output[tid] = sum / count;
    }
}

void sma_gpu(const int *input, int *output, int size, int sample)
{
    int *devInput;
    int *devOutput;
    cudaMalloc((void **)&devInput, sizeof(int) * size);
    cudaMalloc((void **)&devOutput, sizeof(int) * size);
    cudaMemcpy(devInput, input, sizeof(int) * size, cudaMemcpyHostToDevice);
    int blockSize = 256;
    int numBlocks = (size + blockSize - 1) / blockSize;
    sma_gpu_kernel<<<numBlocks, blockSize>>>(devInput, devOutput, size, sample);
    cudaMemcpy(output, devOutput, sizeof(int) * size, cudaMemcpyDeviceToHost);
    cudaFree(devInput);
    cudaFree(devOutput);
}

void sma_cpu(const int *input, int *output, int size, int sample)
{
    int sum = 0;
    int count = 0;
    for (int i = 0; i < size; i++) {
        if (count < sample) {
            sum += input[i];
            count++;
            output[i] = sum / count;
        } else {
            sum -= input[i-sample];
            sum += input[i];
            output[i] = sum / count;
        }
    }
}

void print(int *output, int size)
{
    for (int i = 0; i < size; i++) {
        cout << output[i] << " ";
    }
    cout << endl;
}

void benchmark_sma(int size, int sample) {
    int *input = (int *) malloc(sizeof(int) * size);
    for (int i = 0; i < size; i++) {
        input[i] = (int) (rand() % size);
    }
    /* begin */
    this_thread::sleep_for(200ms);
    int *output1 = (int *) malloc(sizeof(int) * size);
    memset(output1, 0, sizeof(int) * size);
    auto t0 = high_resolution_clock::now();
    sma_cpu(input, output1, size, sample);
    //print(output1, size);
    auto d1 = duration_cast<std::chrono::milliseconds>(high_resolution_clock::now() - t0);
    this_thread::sleep_for(200ms);
    int *output2 = (int *) malloc(sizeof(int) * size);
    memset(output2, 0, sizeof(int) * size);
    t0 = high_resolution_clock::now();
    sma_gpu(input, output2, size, sample);
    //print(output2, size);
    auto d2 = duration_cast<std::chrono::milliseconds>(high_resolution_clock::now() - t0);
    int b = memcmp(output1, output2, sizeof(int) * size);
    cout << "benchmark sma: size=" << size << " sample=" << sample << " equal=" << b << endl;
    cout << "sma_cpu=" << d1.count() << "ms sma_gpu=" << d2.count() << "ms" << endl;
    free(output1);
    free(output2);
}

int main()
{
    /* hot run */
    benchmark_sma(1024*1024, 5);
    /* run */
    benchmark_sma(1024*1024, 5);
    benchmark_sma(32*1024*1024, 5);
    benchmark_sma(1024*1024*1024, 5);
    benchmark_sma(1024*1024, 30);
    benchmark_sma(32*1024*1024, 30);
    benchmark_sma(1024*1024*1024, 30);
    return 0;
}
