# This is the source code of VMGramClient
# It is licensed under GNU GPL v. 2 or later.
# You should have received a copy of the license in this archive (see LICENSE).
# Copyright Voicemod S.L, 2023.

cmake_minimum_required(VERSION 3.22.1)

set(CMAKE_CXX_STANDARD 17)
set(CXX_STANDARD ${CMAKE_CXX_STANDARD})
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(VMGRAMDEVELOPAPP_VERSION 0.0.0)
set(VMGRAMDEVELOPAPP_LANGUAGES CXX)
project(VMGramClient VERSION ${VMGRAMDEVELOPAPP_VERSION} LANGUAGES ${VMGRAMDEVELOPAPP_LANGUAGES})

find_library(log-lib log)
find_library(android-lib android)
find_library(binder_ndk-lib binder_ndk)

option(ENABLE_OPTIMIZATION "Optimization flags" ON)

find_package(VMGramAidl REQUIRED CONFIG)

#set(CMAKE_MODULE_PATH ${CMAKE_CURRENT_SOURCE_DIR}/../../../../CrossCompileUtils/cmake)
#include(CompilerFlags)

add_library(${PROJECT_NAME} SHARED
    net/voicemod/vmgramservice/src/VMGramClient.cpp
)

target_include_directories(${PROJECT_NAME}
    PUBLIC net/voicemod/vmgramservice/include
)

target_link_libraries(${PROJECT_NAME} PRIVATE
    ${log-lib}
    ${android-lib} # TODO: Maybe not needed
    ${binder_ndk-lib}
    VMGramAidl::VMGramAidl
)
if(ENABLE_OPTIMIZATION)
    target_compile_options(${PROJECT_NAME} PRIVATE
        -O3
        -Ofast
        -s
        -g0
        -fno-exceptions
        -fno-rtti
        -fomit-frame-pointer
        -fpic
        -funroll-loops
        -fstrict-aliasing
        #-ffast-math
    )
endif()

#cf_stop_at_first_error(VMGramClient)
#cf_enable_pedantic_flags(VMGramClient)
#target_compile_options(VMGramClient PRIVATE -O3 -Ofast)
