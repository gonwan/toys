#!/bin/sh
export CLASSPATH=.:target/classes:target/lib/lucene-core-4.7.2.jar:target/lib/lucene-analyzers-common-4.7.2.jar:target/lib/lucene-queryparser-4.7.2.jar

d=docs
if [ -n "$1" ]; then
    d=$1
fi

java org.apache.lucene.demo.IndexFiles -docs $d

