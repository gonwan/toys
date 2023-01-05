cmake_minimum_required(VERSION 3.20)
project(testlog4cxx)
# required when built using vcpkg/log4cxx
set(CMAKE_CXX_STANDARD 17)
#set(CMAKE_CXX_FLAGS_RELEASE "-fno-inline")
set(Boost_NO_WARN_NEW_VERSIONS ON)

find_package(log4cxx CONFIG REQUIRED)
find_package(fmt CONFIG REQUIRED)
#find_package(Boost REQUIRED COMPONENTS stacktrace_addr2line)

add_executable(${PROJECT_NAME} main.cpp)

# addr2line is most portable and stable approach so far, though it invokes fork().
# boost-stacktrace is header-only by default, use macro to switch between backends.
# see: https://www.boost.org/doc/libs/1_75_0/doc/html/stacktrace/configuration_and_build.html
target_compile_definitions(${PROJECT_NAME} PRIVATE BOOST_STACKTRACE_USE_ADDR2LINE)
#target_compile_definitions(${PROJECT_NAME} PRIVATE BOOST_STACKTRACE_USE_BACKTRACE)

# vcpkg install log4cxx fmt boost-stacktrace --triplet=x64-linux-dynamic
# cmake -B build -S . -DCMAKE_TOOLCHAIN_FILE=/opt/vcpkg/scripts/buildsystems/vcpkg.cmake -DVCPKG_TARGET_TRIPLET=x64-linux-dynamic
if(${CMAKE_BUILD_TYPE} STREQUAL "Debug")
    # for libbacktrace without cmake config file
    target_link_directories(${PROJECT_NAME} PRIVATE ${VCPKG_INSTALLED_DIR}/${VCPKG_TARGET_TRIPLET}/debug/lib)
else()
    target_link_directories(${PROJECT_NAME} PRIVATE ${VCPKG_INSTALLED_DIR}/${VCPKG_TARGET_TRIPLET}/lib)
endif()
target_link_libraries(${PROJECT_NAME} PRIVATE dl)
target_link_libraries(${PROJECT_NAME} PRIVATE log4cxx)
target_link_libraries(${PROJECT_NAME} PRIVATE fmt::fmt)
target_link_libraries(${PROJECT_NAME} PRIVATE backtrace)
