@echo off
set CLASSPATH=.;target\classes;target\lib\lucene-core-4.7.2.jar;target\lib\lucene-analyzers-common-4.7.2.jar;target\lib\lucene-queryparser-4.7.2.jar

java org.apache.lucene.demo.SearchFiles

