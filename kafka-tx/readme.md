## Kafka Transaction

### Transactional Producer
- `spring.kafka.producer.transaction-id-prefix` auto configures a transaction manager, and implies `enable.idempotence`.
- Exactly Once only applies to `read-process-write` as a whole workflow. It's your duty to confirm this if your application only write to Kafka.

### Transactional Consumer
- `spring.kafka.consumer.enable-auto-commit` defaults to false starting 2.3, see `KafkaMessageListenerContainer`. Should be false in transactions.
- When transaction is not used, `SeekToCurrentErrorHandler` and `RecoveringBatchErrorHandler` are default error handlers. See `KafkaMessageListenerContainer`.
- When transaction is used, no error handlers are configured, by default, so that the exception will roll back the transaction. But similar functionality is provided by `DefaultAfterRollbackProcessor`.
- There is no recoverer to be used with `AfterRollbackProcessor` in a batch listener. The listener must handle records that keep failing.
- When `EOSMode.ALPHA` is used, set container property `subBatchPerPartition` to true to support zombie fencing. 
- When synchronizing transactions, use `ChainedKafkaTransactionManager` with `Best Effort 1PC` pattern. So better to have message deduplication.

### Batch Processing
- 

### Stateful Streaming
- Kafka Streams
  - [https://docs.confluent.io/platform/current/streams/architecture.html#fault-tolerance](https://docs.confluent.io/platform/current/streams/architecture.html#fault-tolerance)
  - For each state store, it maintains a replicated changelog Kafka topic in which it tracks any state updates. These changelog topics are partitioned as well so that each local state store instance, and hence the task accessing the store, has its own dedicated changelog topic partition.
  - To minimize this restoration time, you can configure your applications to have standby replicas of local states, which are fully replicated copies of the state. When a task migration happens, Kafka Streams assigns a task to an application instance where such a standby replica already exists, to minimize the task (re)initialization cost.
  - Only supports reset(from start).
- Apache Flink
  - `RocksDBStateBackend`: [https://ci.apache.org/projects/flink/flink-docs-release-1.12/learn-flink/fault_tolerance.html](https://ci.apache.org/projects/flink/flink-docs-release-1.12/learn-flink/fault_tolerance.html)
    - Working State: Local disk (tmp dir)
    - State Backup: Distributed file system
    - Snapshotting: Full / Incremental
  - Error handling: use side output.

### Others
- `spring-integration-kafka` is built on top of `spring-kafka`, with support for EAI(Enterprise Integration Application) patterns. But seems not so flexible to use.
