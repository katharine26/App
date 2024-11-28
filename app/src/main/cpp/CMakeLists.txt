# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)

# Declares the project name. The project name can be accessed via ${ PROJECT_NAME},
# Since this is the top level CMakeLists.txt, the project name is also accessible
# with ${CMAKE_PROJECT_NAME} (both CMake variables are in-sync within the top level
# build script scope).
project("drawingapp")

# Add the native-lib library
add_library(${CMAKE_PROJECT_NAME} SHARED drawingapp.cpp)

# Find the Android log and graphics libraries
find_library(log-lib log)
find_library(graphics-lib jnigraphics)

# Link the native-lib library against the Android log and graphics libraries
target_link_libraries(${CMAKE_PROJECT_NAME} ${log-lib} ${graphics-lib})