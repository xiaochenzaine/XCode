; Sora bracket query for C/C++.
; Keep this file ASCII-only.
; Each pattern captures one matching pair in the same syntax node.

(compound_statement
  "{" @editor.brackets.open
  "}" @editor.brackets.close)

(initializer_list
  "{" @editor.brackets.open
  "}" @editor.brackets.close)

(field_declaration_list
  "{" @editor.brackets.open
  "}" @editor.brackets.close)

(enumerator_list
  "{" @editor.brackets.open
  "}" @editor.brackets.close)

(parameter_list
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(argument_list
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(parenthesized_expression
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(condition_clause
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(subscript_argument_list
  "[" @editor.brackets.open
  "]" @editor.brackets.close)

(template_argument_list
  "<" @editor.brackets.open
  ">" @editor.brackets.close)

(template_parameter_list
  "<" @editor.brackets.open
  ">" @editor.brackets.close)
