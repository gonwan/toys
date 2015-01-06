#!/bin/sh

export CLASSPATH=.:../lib/lucene-core-4.7.2.jar:../lib/lucene-analyzers-common-4.7.2.jar:../lib/lucene-queryparser-4.7.2.jar

docs=../docs
if [ -n "$1" ]; then
    docs=$1
fi

cd bin
java org.apache.lucene.demo.IndexFiles -docs $docs
cd ..

