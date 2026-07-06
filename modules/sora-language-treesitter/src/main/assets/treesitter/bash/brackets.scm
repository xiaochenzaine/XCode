; Sora bracket query for Bash/Shell.
; Match common syntactic bracket pairs.

(subshell
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(array
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(parenthesized_expression
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(compound_statement
  "{" @editor.brackets.open
  "}" @editor.brackets.close)

(expansion
  "${" @editor.brackets.open
  "}" @editor.brackets.close)

(command_substitution
  "$(" @editor.brackets.open
  ")" @editor.brackets.close)

(arithmetic_expansion
  "$((" @editor.brackets.open
  "))" @editor.brackets.close)

(test_command
  "[" @editor.brackets.open
  "]" @editor.brackets.close)

(test_command
  "[[" @editor.brackets.open
  "]]" @editor.brackets.close)

(subscript
  "[" @editor.brackets.open
  "]" @editor.brackets.close)

(process_substitution
  "<(" @editor.brackets.open
  ")" @editor.brackets.close)

(process_substitution
  ">(" @editor.brackets.open
  ")" @editor.brackets.close)
