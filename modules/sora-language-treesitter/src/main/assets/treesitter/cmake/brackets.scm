; Sora bracket query for CMake.
; Match command parentheses and variable braces.

(argument_list
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(normal_command
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(function_command
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(macro_command
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(block_command
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(if_command
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(elseif_command
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(else_command
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(endif_command
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(foreach_command
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(endforeach_command
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(while_command
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(endwhile_command
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(endfunction_command
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(endmacro_command
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(endblock_command
  "(" @editor.brackets.open
  ")" @editor.brackets.close)

(normal_var
  "{" @editor.brackets.open
  "}" @editor.brackets.close)

(env_var
  "{" @editor.brackets.open
  "}" @editor.brackets.close)

(cache_var
  "{" @editor.brackets.open
  "}" @editor.brackets.close)
