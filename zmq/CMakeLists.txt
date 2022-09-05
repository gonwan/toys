# cmake -G "Visual Studio 16 2019" -DCMAKE_TOOLCHAIN_FILE=C:/Users/gonwan/Source/Repos/vcpkg/scripts/buildsystems/vcpkg.cmake
# cmake -G "Unix Makefiles" -DCMAKE_BUILD_TYPE=debug
cmake_minimum_required(VERSION 3.2)
project(zmq C)

# see: https://github.com/microsoft/vcpkg/blob/master/docs/users/buildsystems/integration.md
if (WIN32)
  # see: ZeroMQConfig.cmake
  find_package(ZeroMQ REQUIRED)
endif ()

file(GLOB client_source_files client.c)
file(GLOB server_source_files server.c)
source_group("Source Files" FILES ${client_source_files})
source_group("Source Files" FILES ${server_source_files})

add_executable(client ${client_source_files})
add_executable(server ${server_source_files})

if (WIN32)
  # see: ZeroMQTargets.cmake
  target_link_libraries(client libzmq)
  target_link_libraries(server libzmq)
elseif (UNIX)
  target_link_libraries(client zmq)
  target_link_libraries(server zmq)
endif ()

if (WIN32)
  #set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} /W4 /WX")
elseif (UNIX)
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra")
endif ()

