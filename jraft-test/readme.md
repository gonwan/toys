### JRaft Test

- Almost copied from the official [sample](https://github.com/sofastack/sofa-jraft/tree/master/jraft-example/src/main/java/com/alipay/sofa/jraft/example/counter).
- Bolt is used by default, include `rpc-grpc-impl` dependency to use Grpc.
- JRaft uses `RocksDB` as persistent storage for log by default.
- Call graph: `IncrementAndGetRequestProcessor#handleRequest()` --> `CounterServiceImpl#incrementAndGet()` --> `CounterServiceImpl#applyOperation()` --> `CounterStateMachine#apply()`. 
- Run `mvn clean compile assembly:single` to package.
- Run using IDEA: create 3 server configurations and 1 client configuration.
