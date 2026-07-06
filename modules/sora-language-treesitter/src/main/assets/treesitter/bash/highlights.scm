[(string) (raw_string) (heredoc_body) (heredoc_start)] @string
(command_name) @function
(function_definition name: (word) @function)
(variable_name) @property
(file_descriptor) @number
(comment) @comment
[
  "case" "do" "done" "elif" "else" "esac" "export" "fi" "for" "function" "if" "in" "select" "then" "unset" "until" "while"
] @keyword
["$" "&&" ">" ">>" "<" "|" "&" "||" ";"] @operator
[(command_substitution) (process_substitution) (expansion)] @string.special
((command (_) @constant) (#match? @constant "^-"))
