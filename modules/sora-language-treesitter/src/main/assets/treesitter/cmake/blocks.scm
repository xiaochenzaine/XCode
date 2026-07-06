; Sora code block query for CMake.
; Semantic command blocks and multi-line commands are marked.

(function_def) @block
(macro_def) @block
(block_def) @block
(if_condition) @block
(foreach_loop) @block
(while_loop) @block

; Plain commands are marked as blocks, covering multi-line add_executable(...), target_link_libraries(...), etc.
; TsAnalyzeManager filters single-line or short blocks.
(normal_command) @block
(function_command) @block
(macro_command) @block
(block_command) @block
(if_command) @block
(elseif_command) @block
(else_command) @block
(foreach_command) @block
(while_command) @block
