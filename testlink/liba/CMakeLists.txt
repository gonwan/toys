cmake_minimum_required(VERSION 3.20)
#project(liba)
set(CMAKE_CXX_COMPILER /usr/bin/g++)  # 4.8/centos7
set(CMAKE_CXX_STANDARD 98)
add_library(a SHARED A.cpp)
