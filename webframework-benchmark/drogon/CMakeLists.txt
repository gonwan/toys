cmake_minimum_required(VERSION 3.20)
project(drogon_server)

set(CMAKE_CXX_STANDARD 14)

find_package(Drogon CONFIG REQUIRED)

add_executable(${PROJECT_NAME} main.cpp)

# vcpkg install drogon --triplet=x64-linux-dynamic
# cmake -B build -S . -DCMAKE_TOOLCHAIN_FILE=/opt/vcpkg/scripts/buildsystems/vcpkg.cmake -DVCPKG_TARGET_TRIPLET=x64-linux-dynamic
target_link_libraries(${PROJECT_NAME} PRIVATE Drogon::Drogon)
