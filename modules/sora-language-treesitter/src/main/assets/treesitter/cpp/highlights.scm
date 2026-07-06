; Stable Sora-compatible C/C++ highlights.
; Keep this file ASCII-only.

(comment) @comment

(string_literal) @string
(system_lib_string) @string
(raw_string_literal) @string
(char_literal) @string
(escape_sequence) @string.special

(number_literal) @number
(null) @constant.builtin
(true) @constant.builtin
(false) @constant.builtin

(type_identifier) @type
(primitive_type) @type.builtin
(auto) @type.builtin
(field_identifier) @property
(statement_identifier) @label
(namespace_identifier) @namespace
(this) @variable.builtin
(identifier) @variable

((identifier) @constant
 (#match? @constant "^[A-Z][A-Z\\d_]*$"))

[
 "break" "case" "continue" "default" "do" "else" "for" "goto" "if" "return" "switch" "while"
 "catch" "co_await" "co_return" "co_yield" "throw" "try"
 "delete" "new" "sizeof" "operator"
 "import" "export" "module"
] @keyword

[
 "const" "constexpr" "constinit" "consteval" "extern" "inline" "static" "typedef" "using" "volatile"
 "explicit" "final" "friend" "mutable" "noexcept" "override" "virtual" "typename" "concept" "requires"
 "register" "thread_local" "restrict"
] @keyword.modifier

[
 "enum" "struct" "union" "class" "namespace" "template" "private" "protected" "public"
] @keyword

[
 "#define" "#elif" "#else" "#endif" "#if" "#ifdef" "#ifndef" "#include"
] @keyword.directive

(preproc_directive) @keyword.directive

[
 "--" "-" "-=" "->" "->*" "=" "!" "!=" "*" "*=" "/" "/=" "%" "%=" "&" "&&" "&="
 "+" "++" "+=" "<" "<<" "<<=" "<=" "==" ">" ">=" ">>" ">>=" "^" "^=" "|" "||" "|=" "~"
 "." ".*" "::"
] @operator

[";" "," ":"] @punctuation
["(" ")" "[" "]" "{" "}"] @punctuation.bracket

(call_expression
  function: (identifier) @function)

(call_expression
  function: (field_expression
    field: (field_identifier) @function.method))

(call_expression
  function: (qualified_identifier
    name: (identifier) @function))

(function_declarator
  declarator: (identifier) @function)

(function_declarator
  declarator: (qualified_identifier
    name: (identifier) @function))

(function_declarator
  declarator: (field_identifier) @function.method)

(preproc_function_def
  name: (identifier) @function.macro)

(preproc_def
  name: (identifier) @constant)

(template_function
  name: (identifier) @function)

(template_method
  name: (field_identifier) @function.method)

(type_parameter_declaration) @type.parameter
(module_name) @module
