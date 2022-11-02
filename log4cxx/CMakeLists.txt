cmake_minimum_required(VERSION 3.20)
project(testlog4cxx)
# required when built using vcpkg/log4cxx
set(CMAKE_CXX_STANDARD 17)
#set(CMAKE_CXX_FLAGS_RELEASE "-fno-inline")
set(Boost_NO_WARN_NEW_VERSIONS ON)

find_package(log4cxx CONFIG REQUIRED)
find_package(fmt CONFIG REQUIRED)
find_package(Boost REQUIRED COMPONENTS stacktrace_addr2line)

add_executable(${PROJECT_NAME} main.cpp)

# addr2line is most portable and stable approach so far, though it invokes fork().
# libbacktrace is not available in centos, and hard to build with vcpkg/boost-stacktrace.
target_compile_definitions(${PROJECT_NAME} PRIVATE BOOST_STACKTRACE_USE_ADDR2LINE)

# vcpkg install log4cxx fmt boost-stacktrace --triplet=x64-linux-dynamic
# cmake -B build -S . -DCMAKE_TOOLCHAIN_FILE=/opt/vcpkg/scripts/buildsystems/vcpkg.cmake -DVCPKG_TARGET_TRIPLET=x64-linux-dynamic
target_link_libraries(${PROJECT_NAME} PRIVATE dl)
target_link_libraries(${PROJECT_NAME} PRIVATE log4cxx)
target_link_libraries(${PROJECT_NAME} PRIVATE fmt::fmt)
target_link_libraries(${PROJECT_NAME} PRIVATE Boost::boost Boost::stacktrace_addr2line)
