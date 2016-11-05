 # Copyright (C) 2011 Aldebaran Robotics
cmake_minimum_required(VERSION 2.6.4 FATAL_ERROR)

# Define the name of the project
project(server)

# This include enable you to use qibuild framework
find_package(qibuild)


# Create an option to make is possible compiling the module
# as a remote executable, or as a local shared library
option(SERVER_IS_REMOTE
  "module is compiled as a remote module (ON or OFF)"
  ON)

# Create a list of source files
set(_srcs
  server.c
)

if(SERVER_IS_REMOTE)
  # Add a compile flag because code changes a little bit
  # when we are compiling an executable
  # This will let you use #ifdef SERVER_IS_REMOTE
  # in the C++ code
  add_definitions( " -DSERVER_IS_REMOTE ")

  # Create an executable
  qi_create_bin(server ${_srcs})
else()
  # Create a plugin, that is a shared library, and make
  # sure it is built in lib/naoqi, so that the naoqi executable
  # can find it later
  qi_create_lib(server SHARED ${_srcs} SUBFOLDER naoqi)
endif()

# Tell CMake that sayhelloworld depends on ALCOMMON.
# This will set the libraries to link sayhelloworld with,
# the include paths, and so on
qi_use_lib(server ALCOMMON)


