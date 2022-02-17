## IDGen Distributed

### Investigated multiple implementations:
- [Twitter/snowflake](https://github.com/twitter-archive/snowflake)
- [Sony/sonyflake](https://github.com/sony/sonyflake)
- [Baidu/uid-generator](https://github.com/baidu/uid-generator)
- [Meituan/Leaf](https://github.com/Meituan-Dianping/Leaf)
- [Didi/tinyid](https://github.com/didi/tinyid)
- [Netease/camellia](https://github.com/netease-im/camellia)
- [Butterfly](https://github.com/SimonAlong/Butterfly)
- [IdGenerator](https://github.com/yitter/IdGenerator)

### Snowflake
- Pros: super fast, no predicatable sequence, no database dependencies.
- Cons: clock time backwards movement, worker id assignment required, no business tagging support.

### Segment
- Pros: no predicatable sequence, business tagging support.
- Cons: database bottleneck(double buffer for improvement).

### Considerations
- Snowflake is much faster than Segment approach. But it is not so easy for operation.
- The open source versions are not well-maintained. [Netease/camellia](https://github.com/netease-im/camellia) seems to be the winner so far.
