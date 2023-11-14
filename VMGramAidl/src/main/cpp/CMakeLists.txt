# This is the source code of VMGramClient
# It is licensed under GNU GPL v. 2 or later.
# You should have received a copy of the license in this archive (see LICENSE).
# Copyright Voicemod S.L, 2023.

cmake_minimum_required(VERSION 3.22.1)

set(CMAKE_CXX_STANDARD 17)
set(CXX_STANDARD ${CMAKE_CXX_STANDARD})
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(VMGSAIDL_VERSION 0.0.0)
set(VMGSAIDL_LANGUAGES CXX)

project(VMGramAidl VERSION ${VMGSAIDL_VERSION} LANGUAGES ${VMGSAIDL_LANGUAGES})

add_library(${PROJECT_NAME} SHARED
    net/voicemod/vmgramservice/VMGramAidl.cpp
)

find_library(log-lib log)
find_library(binder_ndk-lib binder_ndk)

target_link_libraries(${PROJECT_NAME} PUBLIC
    ${binder_ndk-lib}
    ${log-lib}
)

target_include_directories(${PROJECT_NAME} PUBLIC
    include
)
