cmake_minimum_required(VERSION 3.20)
project(drogon_server)

set(CMAKE_CXX_STANDARD 17)
if(WIN32)
  add_definitions(-D_WIN32_WINNT=0x0603)
endif()

find_path(APR_INCLUDE_DIR apr.h)
find_path(APR_UTIL_INCLUDE_DIR apr_thread_pool.h)
include_directories(${APR_INCLUDE_DIR}$ ${APR_UTIL_INCLUDE_DIR})
find_package(Drogon CONFIG REQUIRED)
find_package(SOCI CONFIG REQUIRED)

add_executable(${PROJECT_NAME} drogon_server.cpp)

# vcpkg install drogon --triplet=x64-linux-dynamic
# cmake -B build -S . -DCMAKE_TOOLCHAIN_FILE=/opt/vcpkg/scripts/buildsystems/vcpkg.cmake -DVCPKG_TARGET_TRIPLET=x64-linux-dynamic
if(${VCPKG_BUILD_TYPE} STREQUAL "Debug")
  target_link_directories(${PROJECT_NAME} PRIVATE ${VCPKG_INSTALLED_DIR}/${VCPKG_TARGET_TRIPLET}/debug/lib)
else()
  target_link_directories(${PROJECT_NAME} PRIVATE ${VCPKG_INSTALLED_DIR}/${VCPKG_TARGET_TRIPLET}/lib)
endif()
# for libapr/libaprutil without cmake config file
if(WIN32)
  find_library(APR_LIBRARY libapr-1)
  find_library(APR_UTIL_LIBRARY libaprutil-1)
elseif(UNIX)
  find_library(APR_LIBRARY apr-1)
  find_library(APR_UTIL_LIBRARY aprutil-1)
endif()

target_link_libraries(${PROJECT_NAME} PRIVATE ${APR_LIBRARY} ${APR_UTIL_LIBRARY})
target_link_libraries(${PROJECT_NAME} PRIVATE Drogon::Drogon)
target_link_libraries(${PROJECT_NAME} PRIVATE SOCI::soci_core SOCI::soci_mysql)
