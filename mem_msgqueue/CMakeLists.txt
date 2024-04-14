cmake_minimum_required(VERSION 3.20)
project(mem_msgqueue)

set(CMAKE_CXX_STANDARD 14)
set(Boost_NO_WARN_NEW_VERSIONS ON)

find_package(Boost REQUIRED)

add_executable(mqclient mqclient.cpp)
add_executable(mqserver mqserver.cpp)

# for boost header only libraries
target_link_libraries(mqclient PRIVATE rt pthread Boost::boost)
target_link_libraries(mqserver PRIVATE rt pthread Boost::boost)
