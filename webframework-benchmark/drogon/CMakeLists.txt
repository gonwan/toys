cmake_minimum_required(VERSION 3.20)
project(drogon_server)

set(CMAKE_CXX_STANDARD 14)

find_package(Drogon CONFIG REQUIRED)
find_package(SOCI CONFIG REQUIRED)
find_package(libmysql REQUIRED)

add_executable(${PROJECT_NAME} main.cpp)

# vcpkg install drogon --triplet=x64-linux-dynamic
# cmake -B build -S . -DCMAKE_TOOLCHAIN_FILE=/opt/vcpkg/scripts/buildsystems/vcpkg.cmake -DVCPKG_TARGET_TRIPLET=x64-linux-dynamic
if(${CMAKE_BUILD_TYPE} STREQUAL "Debug")
  # for libapr without cmake config file
  target_link_directories(${PROJECT_NAME} PRIVATE ${VCPKG_INSTALLED_DIR}/${VCPKG_TARGET_TRIPLET}/debug/lib)
else()
  target_link_directories(${PROJECT_NAME} PRIVATE ${VCPKG_INSTALLED_DIR}/${VCPKG_TARGET_TRIPLET}/lib)
endif()
target_link_libraries(${PROJECT_NAME} PRIVATE apr-1)
target_link_libraries(${PROJECT_NAME} PRIVATE aprutil-1)
target_link_libraries(${PROJECT_NAME} PRIVATE Drogon::Drogon)
target_link_libraries(${PROJECT_NAME} PRIVATE SOCI::soci_core SOCI::soci_mysql)
