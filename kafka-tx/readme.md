## Kafka Transaction

### Transactional Producer
- `spring.kafka.producer.transaction-id-prefix` auto configures a transaction manager, and implies `enable.idempotence`.
- Exactly Once only applies to `read-process-write` as a whole workflow. It's your duty to confirm this if your application only write to Kafka.

### Transactional Consumer
- `spring.kafka.consumer.enable-auto-commit` defaults to false starting 2.3, see `KafkaMessageListenerContainer`. Should be false in transactions.
- When transaction is not used, `SeekToCurrentErrorHandler` and `RecoveringBatchErrorHandler` are default error handlers. See `KafkaMessageListenerContainer`.
- When transaction is used, no error handlers are configured, by default, so that the exception will roll back the transaction. But similar functionality is provided by `DefaultAfterRollbackProcessor`.
- There is no recovery with batch listener using transaction. The application listener must handle a record that keeps failing.
- When EOSMode.ALPHA is used, set container property `subBatchPerPartition` to true to support zombie fencing. 
- When synchronizing transactions, use `ChainedKafkaTransactionManager` with `Best Effort 1PC` pattern. So better to have message deduplication.
