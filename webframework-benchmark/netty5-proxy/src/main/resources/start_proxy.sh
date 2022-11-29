#!/bin/sh
ulimit -n 20000
java -jar netty5-proxy-0.0.1-SNAPSHOT.jar &
