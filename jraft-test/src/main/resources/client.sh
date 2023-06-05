#!/bin/bash

export CLASSPATH=jraft-test-1.0-SNAPSHOT-jar-with-dependencies.jar
nohup java com.gonwan.toys.counter.CounterClient counter 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083 &
