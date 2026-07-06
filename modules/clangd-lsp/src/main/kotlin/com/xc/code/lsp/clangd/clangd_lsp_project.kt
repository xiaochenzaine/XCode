package com.xc.code.lsp.clangd

import io.github.rosemoe.sora.lsp.client.languageserver.LspFeature
import io.github.rosemoe.sora.lsp.editor.LspEditor
import io.github.rosemoe.sora.lsp.editor.LspLanguage
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.lsp.editor.LspProject
import io.github.rosemoe.sora.widget.CodeEditor
import java.io.File

class clangd_lsp_project(
    val project_dir: File,
    private val config_factory: (working_dir: String) -> clangd_lsp_config,
    private val disabled_features: Set<LspFeature> = emptySet(),
    private val language_factory: (String) -> Language? = { null }
) {
    val project: LspProject = LspProject(project_dir.absolutePath).apply {
        addServerDefinitions(create_clangd_language_server_definitions(config_factory, disabled_features))
        init()
    }

    fun get_or_create_editor(file: File, editor: CodeEditor): LspEditor {
        val lsp_editor = project.getOrCreateEditor(file.absolutePath)
        val current_language = editor.editorLanguage
        val wrapper_language = if (current_language is LspLanguage) {
            // 编辑器已经安装了 LspLanguage 时，不能再创建新的 wrapperLanguage。
            // CodeEditor.setEditorLanguage() 只会在语言对象变化时重新绑定 AnalyzeManager；
            // 如果这里替换 wrapperLanguage，但 editorLanguage 仍是同一个 LspLanguage，
            // 新的 AnalyzeManager 不会收到 receiver/reset，表现为偶发无高亮。
            current_language.wrapperLanguage ?: language_factory(file.absolutePath)
        } else {
            language_factory(file.absolutePath) ?: current_language
        }
        lsp_editor.wrapperLanguage = wrapper_language
        lsp_editor.editor = editor
        return lsp_editor
    }

    suspend fun connect(file: File, editor: CodeEditor): Boolean {
        return get_or_create_editor(file, editor).connect(throwException = false)
    }

    fun close_file(file: File) {
        project.getEditor(file.absolutePath)?.dispose()
        project.removeEditor(file.absolutePath)
    }

    fun dispose() {
        project.dispose()
    }
}
