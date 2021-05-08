## Benchmark for web frameworks

See [Techempower](https://www.techempower.com/benchmarks/). 
This repository contains homemade java benchmarks using `spring-mvc`, `spring-webflux` and `netty-http`/`netty-tcp` servers based on `reactor-netty`. [gin](https://github.com/TechEmpower/FrameworkBenchmarks/tree/master/frameworks/Go/gin) and [gnet](https://github.com/TechEmpower/FrameworkBenchmarks/tree/master/frameworks/Go/gnet) are also included. [wrk](https://github.com/wg/wrk) is used as client. [gobench](https://github.com/cmpxchg16/gobench/) is also considered but it is not so good as `wrk`.

```
# benchmarking plaintext
# ./wrk -c 1000 -t 30 -d 30s http://10.xx.xx.xx:8124/text
```
### Environment 1
- Server: 8C16G vm
- Client: 4C8G vm * 2
| Server       | Server Throughput | Server CPU |
| ------------------ | ----------------: | ---------: |
| **spring-mvc**     | 25k ~ 30k /s |      ~600% |
| **spring-webflux** | 90k ~ 110k /s |      ~780% |
| **go-gin**         |  110k ~ 120k /s |      ~600% |
| **go-gnet**        |  110k ~ 120k /s |      ~270% |
| **netty-http**     |   110k ~ 120k /s |      ~480% |
| **netty-tcp**      |    110k ~ 120k /s |      ~360% |

2 VM Clients are not able to fully utilize the server capability. The initial attempts were benchmarking only first 4 cases. And the `go-gnet` results made me wonder, it can give much more throughput. After reading the source of it, I found `go-gnet` case is actually a TCP server with very very little of HTTP implementation to fulfil the benchmark. And I add case 5/6 in java to compare with it.

### Environment 2
- Server: 24C32G physical machine
- Client:
  - 4C8G vm * 2
  - 8C16G vm * 1
  - 24C32G physical machine * 1

| Server       | Server Throughput | Server CPU |
| ------------------ | ----------------: | ---------: |
| **spring-mvc**     | ~120k /s |   ~1560% |
| **spring-webflux** | ~180k /s |  ~2380% |
| **go-gin**         |   ~380k /s |   ~2350% |
| **go-gnet**        | 560k ~ 580k /s |  ~1160% |
| **netty-http**     | 560k ~ 580k /s | ~2350% |
| **netty-tcp**      | 560k ~ 580k /s |   ~1460% |

Still room to give more QPS for `go-gnet` and `netty-tcp`. Not having so many idle systems for benchmarking.

As a developer, `spring-mvc` or `go-gin` can still be the first choice, as they are easier to getting started. 

