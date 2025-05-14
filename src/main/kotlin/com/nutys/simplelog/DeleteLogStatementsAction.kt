package com.nutys.simplelog

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange

class DeleteLogStatementsAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project? = e.project
        val editor = e.getData(CommonDataKeys.EDITOR)
        if (project == null || editor == null) {
            return
        }

        val document: Document = editor.document
        // 匹配所有以console.log("SimpleLog=>开头的行
        val logPattern = Regex("^\\s*console\\.log\\(\"SimpleLog=>.*\".*\\)?;?\\s*$")
        
        WriteCommandAction.runWriteCommandAction(project) {
            var lineNumber = 0
            while (lineNumber < document.lineCount) {
                val lineText = document.getText(TextRange(
                    document.getLineStartOffset(lineNumber),
                    document.getLineEndOffset(lineNumber)
                ))
                
                if (logPattern.matches(lineText)) {
                    val start = document.getLineStartOffset(lineNumber)
                    val end = if (lineNumber < document.lineCount - 1) {
                        document.getLineStartOffset(lineNumber + 1)
                    } else {
                        document.getLineEndOffset(lineNumber)
                    }
                    document.deleteString(start, end)
                } else {
                    lineNumber++
                }
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val project: Project? = e.project
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = project != null && editor != null
    }
}