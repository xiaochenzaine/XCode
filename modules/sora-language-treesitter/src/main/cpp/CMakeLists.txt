cmake_minimum_required(VERSION 3.22.1)

project("tree-sitter-cpp")

set(TS_DIR "${CMAKE_CURRENT_SOURCE_DIR}/tree-sitter-lib")

include_directories(
    "${TS_DIR}/lib/include"
    "${TS_DIR}/lib/src"
    "${TS_DIR}/lib/src/tree_sitter"
    "${CMAKE_CURRENT_SOURCE_DIR}/cpp"
    "${CMAKE_CURRENT_SOURCE_DIR}/json"
    "${CMAKE_CURRENT_SOURCE_DIR}/bash"
    "${CMAKE_CURRENT_SOURCE_DIR}/cmake"
    "${CMAKE_CURRENT_SOURCE_DIR}/yaml"
)

add_library(tree-sitter-cpp SHARED
    "${TS_DIR}/lib/src/lib.c"
    "cpp/parser.c"
    "cpp/scanner.c"
    "cpp/tree-sitter-cpp.cpp"
    "json/parser.c"
    "bash/parser.c"
    "bash/scanner.c"
    "cmake/parser.c"
    "cmake/scanner.c"
    "yaml/parser.c"
    "yaml/scanner.c"
    "grammar_bindings.cpp"
)

if (${CMAKE_SYSTEM_NAME} STREQUAL Android)
    find_library(log-lib log)
    target_link_libraries(tree-sitter-cpp ${log-lib})
endif()
