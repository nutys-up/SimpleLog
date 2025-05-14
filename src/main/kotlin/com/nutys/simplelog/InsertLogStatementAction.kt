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
            // Clear selection to prevent replacing selected text
            if (selectionModel.hasSelection()) {
                selectionModel.removeSelection()
            }
            
            val caretModel = editor.caretModel
            val currentLine = document.getLineNumber(caretModel.offset)
            val lineStartOffset = document.getLineStartOffset(currentLine)
            val lineText = document.getText(com.intellij.openapi.util.TextRange(lineStartOffset, document.getLineEndOffset(currentLine)))
            val indent = lineText.takeWhile { it.isWhitespace() }

            // Determine the insertion point: end of the current line to ensure the new log is on the next line
            val insertOffset = document.getLineEndOffset(currentLine)
            val textToInsert = "\n" + indent + logStatement
            
            document.insertString(insertOffset, textToInsert)
            // Move caret to the end of the inserted log statement
            caretModel.moveToOffset(insertOffset + textToInsert.length)
        }
    }

    override fun update(e: AnActionEvent) {
        val project: Project? = e.project
        val editor: Editor? = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = project != null && editor != null
    }
}