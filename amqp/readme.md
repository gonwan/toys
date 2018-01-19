## amqp - AMQP Snippet

### Features

- Requires Java 1.8+.
- Works only with RabbitMQ (AMQP 0.9.1)

### Basic Usage

- sender.bat / sender.sh
```
# ./sender.sh
=== Message sender for RabbitMQ ===
Usage: sender [options]
  Options:
    -h, --host
      host of broker
      Default: 127.0.0.1
    -p, --port
      port of broker
      Default: 5672
    -q, --queue
      queue name
      Default: <empty string>
    -e, --exchange
      exchange name
      Default: <empty string>
    -t, --type
      exchange type
      Default: fanout
    -r, --routing-key
      routing key
      Default: <empty string>
    -m, --message
      message to send
      Default: <empty string>
    -f, --file
      file to read messages to send
      Default: <empty string>
    -i, --interval
      send interval: in millisecond
      Default: 1000
    -b, --batch
      send mode: line by line or as a whole
      Default: false
```

- receiver.bat / receiver.sh
```
# ./receiver.sh
=== Message receiver for RabbitMQ ===
Usage: receiver [options]
  Options:
    -h, --host
      host of broker
      Default: 127.0.0.1
    -p, --port
      port of broker
      Default: 5672
    -q, --queue
      queue name
      Default: <empty string>
    -e, --exchange
      exchange name
      Default: <empty string>
    -t, --type
      exchange type
      Default: fanout
    -r, --routing-key
      routing key
      Default: <empty string>
    -f, --file
      file to write received messages
      Default: <empty string>
    -v, --verbose
      whether to print received messages
      Default: false
```

- Queue (Direct)
```
# ./receiver.sh -h 172.16.87.11 -q test.queue -v
# ./sender.sh -h 172.16.87.11 -q test.queue -m "test queue message..."
```

- Fanout
```
# ./receiver.sh -h 172.16.87.11 -e test.fanout -t fanout -r key1 -v
# ./receiver.sh -h 172.16.87.11 -e test.fanout -t fanout -r key2 -v
# ./sender.sh -h 172.16.87.11 -e test.fanout -t fanout -m "test fanout message..."
```

- Topic
```
# ./receiver.sh -h 172.16.87.11 -e test.topic -t topic -r key1.* -v
# ./receiver.sh -h 172.16.87.11 -e test.topic -t topic -r *.key2 -v
# ./sender.sh -h 172.16.87.11 -e test.topic -t topic -r abcd.efgh -m "test topic message..."
# ./sender.sh -h 172.16.87.11 -e test.topic -t topic -r key1.efgh -m "test topic message..."
# ./sender.sh -h 172.16.87.11 -e test.topic -t topic -r abcd.key2 -m "test topic message..."
# ./sender.sh -h 172.16.87.11 -e test.topic -t topic -r key1.key2 -m "test topic message..."
```

