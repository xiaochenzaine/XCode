; Stable Sora-compatible CMake highlights.
; Keep this file ASCII-only.

[(bracket_comment) (line_comment)] @comment

[(quoted_argument) (bracket_argument)] @string
(escape_sequence) @string.special

(variable) @variable
(variable_ref) @variable
(normal_var) @variable
(env_var) @property
(cache_var) @property

(identifier) @function

((identifier) @function.builtin
 (#match? @function.builtin "^(cmake_minimum_required|project|enable_language|add_executable|add_library|add_subdirectory|target_link_libraries|target_include_directories|target_compile_definitions|target_compile_options|target_sources|target_link_options|target_precompile_headers|target_compile_features|set|unset|option|message|find_package|find_library|find_path|find_file|find_program|include|include_directories|link_directories|link_libraries|file|configure_file|install|export|list|string|math|execute_process|add_custom_command|add_custom_target|add_dependencies|set_property|get_property|get_target_property|set_target_properties|get_filename_component|cmake_path|fetchcontent_declare|fetchcontent_makeavailable)$"))

[
 (function) (endfunction) (macro) (endmacro) (block) (endblock)
 (if) (elseif) (else) (endif) (foreach) (endforeach) (while) (endwhile)
] @keyword

((unquoted_argument) @keyword.modifier
 (#match? @keyword.modifier "^(PUBLIC|PRIVATE|INTERFACE|STATIC|SHARED|MODULE|OBJECT|ALIAS|IMPORTED|GLOBAL|CACHE|FORCE|PARENT_SCOPE|REQUIRED|QUIET|CONFIG|EXACT|COMPONENTS|OPTIONAL_COMPONENTS|NO_DEFAULT_PATH|BEFORE|AFTER|SYSTEM|EXCLUDE_FROM_ALL|COMMAND|DEPENDS|WORKING_DIRECTORY|COMMENT|VERBATIM|OUTPUT|BYPRODUCTS|MAIN_DEPENDENCY|ARGS|DESTINATION|TARGETS|FILES|DIRECTORY|RUNTIME|LIBRARY|ARCHIVE|INCLUDES|EXPORT|NAMESPACE|LANGUAGES|VERSION|PROPERTIES|PERMISSIONS|OWNER_EXECUTE|POST_BUILD|PRE_BUILD|PRE_LINK|GLOB|GLOB_RECURSE)$"))

((unquoted_argument) @constant.builtin
 (#match? @constant.builtin "^(ON|OFF|TRUE|FALSE|YES|NO|Y|N|IGNORE|NOTFOUND|C|CXX|CUDA|OBJC|OBJCXX|ASM)$"))

((unquoted_argument) @number
 (#match? @number "^[0-9]+(\\.[0-9]+)*$"))

((unquoted_argument) @property
 (#match? @property "^(CMAKE_[A-Za-z0-9_]+|PROJECT_[A-Za-z0-9_]+|CMAKE_CXX_[A-Za-z0-9_]+|CMAKE_C_[A-Za-z0-9_]+|ANDROID_[A-Za-z0-9_]+|BUILD_[A-Za-z0-9_]+|CMAKE_BUILD_TYPE|IMPORTED_[A-Za-z0-9_]+|INTERFACE_[A-Za-z0-9_]+|RUNTIME_OUTPUT_DIRECTORY|LIBRARY_OUTPUT_DIRECTORY|ARCHIVE_OUTPUT_DIRECTORY|OUTPUT_NAME|POSITION_INDEPENDENT_CODE|CXX_STANDARD|C_STANDARD|CXX_EXTENSIONS|C_EXTENSIONS|COMPILE_OPTIONS|COMPILE_DEFINITIONS|INCLUDE_DIRECTORIES|LINK_LIBRARIES|LINK_OPTIONS|SOURCES)$"))

((unquoted_argument) @property
 (#match? @property "^[A-Z][A-Z0-9_]+$"))

((unquoted_argument) @operator
 (#match? @operator "^(AND|OR|NOT|MATCHES|LESS|GREATER|EQUAL|LESS_EQUAL|GREATER_EQUAL|STREQUAL|STRLESS|STRGREATER|STRLESS_EQUAL|STRGREATER_EQUAL|VERSION_LESS|VERSION_GREATER|VERSION_EQUAL|VERSION_LESS_EQUAL|VERSION_GREATER_EQUAL|IN_LIST|DEFINED|EXISTS|IS_DIRECTORY|IS_SYMLINK|IS_ABSOLUTE)$"))

((unquoted_argument) @string.special
 (#match? @string.special "^\\$<.*>$"))

((unquoted_argument) @string
 (#match? @string "^([A-Za-z0-9_./+-]+\\.(c|cc|cpp|cxx|h|hh|hpp|hxx|cmake|txt|a|so|dll|lib|jar|aar)|[A-Za-z0-9_./+-]+/[A-Za-z0-9_./+-]+)$"))

["ENV" "CACHE"] @keyword.modifier
["$" "{" "}"] @punctuation.special
["(" ")" ";"] @punctuation
