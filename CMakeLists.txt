cmake_minimum_required(VERSION 3.22.1)

project("porcocliquer")

# Compila o arquivo C++ em uma biblioteca nativa .so chamada native-lib
add_library(
    native-lib
    SHARED
    native-lib.cpp
)

# Procura a biblioteca de logs do Android para monitorar bugs
find_library(
    log-lib
    log
)

# Une a biblioteca de logs ao seu arquivo .so
target_link_libraries(
    native-lib
    ${log-lib}
)
