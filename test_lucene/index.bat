@echo off
set CLASSPATH=.;..\lib\lucene-core-4.7.2.jar;..\lib\lucene-analyzers-common-4.7.2.jar;..\lib\lucene-queryparser-4.7.2.jar

set docs=..\docs
if "%~1"=="" goto do_index
set docs="%~1"

:do_index
cd bin
java org.apache.lucene.demo.IndexFiles -docs %docs%
cd ..
