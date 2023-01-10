cmake_minimum_required(VERSION 3.20)
project(drogon_server)

set(CMAKE_CXX_STANDARD 14)

find_package(Drogon CONFIG REQUIRED)
find_package(SOCI CONFIG REQUIRED)
find_package(libmysql REQUIRED)

add_executable(${PROJECT_NAME} main.cpp)

# vcpkg install drogon --triplet=x64-linux-dynamic
# cmake -B build -S . -DCMAKE_TOOLCHAIN_FILE=/opt/vcpkg/scripts/buildsystems/vcpkg.cmake -DVCPKG_TARGET_TRIPLET=x64-linux-dynamic
target_link_libraries(${PROJECT_NAME} PRIVATE Drogon::Drogon)
target_link_libraries(${PROJECT_NAME} PRIVATE SOCI::soci_core SOCI::soci_empty SOCI::soci_mysql)
target_link_libraries(${PROJECT_NAME} PRIVATE ${MYSQL_LIBRARIES})
