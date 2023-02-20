cmake_minimum_required(VERSION 3.20)
project(testlink)
set(CMAKE_CXX_COMPILER /opt/rh/devtoolset-7/root/usr/bin/g++)
set(CMAKE_CXX_STANDARD 11)

add_subdirectory(liba)
add_subdirectory(libb)

add_executable(main main.cpp)
target_link_libraries(main jemalloc a b) # only link to jemalloc, via symbol interposition
