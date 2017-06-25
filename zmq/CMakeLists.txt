# cmake -G "Visual Studio 14 2015" -DCMAKE_TOOLCHAIN_FILE=C:/Users/gonwan/Source/Repos/vcpkg/scripts/buildsystems/vcpkg.cmake
# cmake -G "Unix Makefiles" -DCMAKE_BUILD_TYPE=debug
cmake_minimum_required(VERSION 2.8)
project(zmq C)

# see: https://github.com/Microsoft/vcpkg/blob/master/docs/users/integration.md
if (WIN32)
  include_directories("${_VCPKG_INSTALLED_DIR}\\${VCPKG_TARGET_TRIPLET}\\include")
  set(debug_link_directories ${_VCPKG_INSTALLED_DIR}\\${VCPKG_TARGET_TRIPLET}\\debug\\lib)
  set(optimized_link_directories ${_VCPKG_INSTALLED_DIR}\\${VCPKG_TARGET_TRIPLET}\\lib)
endif ()

file(GLOB client_source_files client.c)
file(GLOB server_source_files server.c)
source_group("Source Files" FILES ${client_source_files})
source_group("Source Files" FILES ${server_source_files})

add_executable(client ${client_source_files})
add_executable(server ${server_source_files})

if (WIN32)
  target_link_libraries(client
    debug "${debug_link_directories}\\libzmq.lib"
    optimized "${optimized_link_directories}\\libzmq.lib"
  )
  target_link_libraries(server
    debug "${debug_link_directories}\\libzmq.lib"
    optimized "${optimized_link_directories}\\libzmq.lib"
  )
elseif (UNIX)
  target_link_libraries(client zmq)
  target_link_libraries(server zmq)
endif ()

if (WIN32)
  #set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} /W4 /WX")
elseif (UNIX)
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra")
endif ()

