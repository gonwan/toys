### JRaft Test

- Almost copied from the official [sample](https://github.com/sofastack/sofa-jraft/tree/master/jraft-example/src/main/java/com/alipay/sofa/jraft/example/counter).
- Bolt is used by default, include `rpc-grpc-impl` dependency to use Grpc.
- JRaft uses `RocksDB` as persistent storage for log by default.
- Call graph: `IncrementAndGetRequestProcessor#handleRequest()` --> `CounterServiceImpl#incrementAndGet()` --> `CounterServiceImpl#applyOperation()` --> `CounterStateMachine#apply()`. 
- Run `mvn clean compile assembly:single` to package.
- Run using IDEA: create 3 server configurations and 1 client configuration.
- Async invoke is ~10x faster than Sync invoke.
- Key components
  - `LogStorage`, `SnapshotStorage` & `RaftMetaStorage`. Snapshot is optional, `NodeOptions#setSnapshotUri()`.
  - `Node#apply()` submits log entries to state machine, `StateMachine#onApply()` applies these log entries.
  - Leader replicator: `NodeImpl#becomeLeader()` --> `ReplicatorGroup#addReplicator()` --> `Replicator#start()` --> `Replicator#sendEmptyEntries()` --> `RaftClientService#installSnapshot()` or `RaftClientService#appendEntries()`.
  - Follower replicator: `AppendEntriesRequestProcessor` & `InstallSnapshotRequestProcessor`. `CopySession` for remote snapshot file copying.
  - A probe request is `AppendEntriesRequest` with 0 entries to send back `last_log_index`, see `NodeImpl#handleAppendEntriesRequest()`.
  - Servers take local snapshots independently, with fixed interval(3600s) by default.
  - Troubleshooting: `kill -s SIGUSR2 pid`, gives `node_metrics.log`, `node_describe.log` & `thread_pool_metrics.log`.
```
2023-06-06 11:28:16.824 [INFO] [NodeImpl] - Node <counter/127.0.0.1:8083> received InstallSnapshotRequest from 127.0.0.1:8081, lastIncludedLogIndex=100451, lastIncludedLogTerm=12, lastLogId=LogId [index=0, term=0].
2023-06-06 11:28:16.952 [INFO] [Replicator] - Node counter:127.0.0.1:8081 received InstallSnapshotResponse from 127.0.0.1:8083 lastIncludedIndex=100451 lastIncludedTerm=12 success=true
```
