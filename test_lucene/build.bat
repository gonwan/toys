@echo off
set CLASSPATH=.;..\lib\lucene-core-4.7.2.jar;..\lib\lucene-analyzers-common-4.7.2.jar;..\lib\lucene-queryparser-4.7.2.jar

cd src
javac -d ..\bin org\apache\lucene\demo\*.java
cd ..
