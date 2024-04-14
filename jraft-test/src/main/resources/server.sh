#!/bin/bash

export CLASSPATH=jraft-test-1.0-SNAPSHOT-jar-with-dependencies.jar
if [ $1 == 1 ]; then
  nohup java com.gonwan.toys.counter.CounterServer counter /tmp/server1 127.0.0.1:8081 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083 &
elif [ $1 == 2 ]; then
  nohup java com.gonwan.toys.counter.CounterServer counter /tmp/server2 127.0.0.1:8082 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083 &
elif [ $1 == 3 ]; then
  nohup java com.gonwan.toys.counter.CounterServer counter /tmp/server3 127.0.0.1:8083 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083 &
fi
