(comment) @comment
[(double_quote_scalar) (single_quote_scalar) (block_scalar) (string_scalar)] @string
[(integer_scalar) (float_scalar)] @number
[(boolean_scalar) (null_scalar)] @constant.builtin
[(anchor_name) (alias_name)] @variable
[(anchor) (alias)] @variable
[(yaml_directive) (tag_directive) (reserved_directive)] @keyword.directive
[(directive_name) (directive_parameter) (tag) (tag_handle) (tag_prefix) (yaml_version)] @keyword
(escape_sequence) @string.special
(block_mapping_pair key: (_) @string.special.key)
(flow_pair key: (_) @string.special.key)
["-" ":" "," "---" "..."] @punctuation
["[" "]" "{" "}"] @punctuation
