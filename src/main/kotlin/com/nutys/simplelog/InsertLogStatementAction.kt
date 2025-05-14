package com.nutys.simplelog

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

class InsertLogStatementAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project? = e.project
        val editor: Editor? = e.getData(CommonDataKeys.EDITOR)
        println("project: $project, editor: $editor")
        if (project == null || editor == null) {
            return
        }

        val document: Document = editor.document
        val selectionModel = editor.selectionModel
        val selectedText = selectionModel.selectedText

        val logStatement: String = if (selectedText != null && selectedText.isNotBlank()) {
            // Sanitize selectedText to be a valid variable name if it's complex
            val variableName = selectedText.trim().replace(Regex("[^a-zA-Z0-9_$\\s]"), "").replace(Regex("\\s+"), "_")
            if (variableName.isNotEmpty()) {
                 // Check if selectedText is already a string literal
                if ((selectedText.startsWith("\"") && selectedText.endsWith("\"")) || 
                    (selectedText.startsWith("'" ) && selectedText.endsWith("'")) || 
                    (selectedText.startsWith("`") && selectedText.endsWith("`"))) {
                    "console.log(\"SimpleLog=>$selectedText\")"
                } else {
                    "console.log(\"SimpleLog=>$variableName\", $variableName)"
                }
            } else {
                "console.log(\"SimpleLog=>\", $selectedText)" // Fallback if sanitization results in empty string
            }
        } else {
            "console.log(\"SimpleLog=>\")"
        }

        WriteCommandAction.runWriteCommandAction(project) {
            val offset = editor.caretModel.offset
            document.insertString(offset, "\n" + logStatement)
            editor.caretModel.moveToOffset(offset + logStatement.length + 1) // +1 for the newline
        }
    }

    override fun update(e: AnActionEvent) {
        val project: Project? = e.project
        val editor: Editor? = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = project != null && editor != null
    }
}