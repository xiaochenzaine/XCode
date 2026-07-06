; Sora code block query for C/C++.
; Keep this file ASCII-only. Tree-sitter query validation rejects non-ASCII text.
; Use compound_statement as the primary block node, matching Sora's official Java block query style.
; Do not also capture function_definition, otherwise ordinary functions can show duplicate block lines.

(compound_statement) @editor.code-block.marked
(class_specifier) @editor.code-block.marked
(struct_specifier) @editor.code-block.marked
(enum_specifier) @editor.code-block.marked
(namespace_definition) @editor.code-block.marked
