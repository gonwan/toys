#!/bin/sh
export CLASSPATH=.:target/lib/lucene-core-4.7.2.jar:target/lib/lucene-analyzers-common-4.7.2.jar:target/lib/lucene-queryparser-4.7.2.jar

javac -d target/classes -source 1.6 -target 1.6 src/main/java/org/apache/lucene/demo/*.java

