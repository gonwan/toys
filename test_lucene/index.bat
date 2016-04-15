@echo off
set CLASSPATH=.;target\classes;target\lib\lucene-core-4.7.2.jar;target\lib\lucene-analyzers-common-4.7.2.jar;target\lib\lucene-queryparser-4.7.2.jar

set d=docs
if "%~1"=="" goto do_index
set d="%~1"

:do_index
java org.apache.lucene.demo.IndexFiles -docs %d%

