package dev.alllexey.client.command.impl

import dev.alllexey.client.ClientApp
import dev.alllexey.client.command.ClientCommand
import dev.alllexey.client.exception.ScriptInputException
import dev.alllexey.common.io.InputProvider
import dev.alllexey.common.model.command.arg
import dev.alllexey.common.model.field.FieldType
import java.io.File

object ExecuteScriptCommand : ClientCommand(
    "execute_script",
    listOf(arg { name = "filename"; type = FieldType.StringType }),
    "считать и исполнить скрипт из указанного файла.",
) {

    override fun execute(args: List<String>, app: ClientApp) {
        val filename = args.getOrNull(0)
        if (filename.isNullOrBlank()) {
            app.err("Название файла не может быть пустым!")
            return
        }

        val file = File(filename).absoluteFile

        if (app.fileStack.any { it.absolutePath == file.absolutePath }) {
            app.err("Обнаружена рекурсия. Скрипт ${file.name} уже выполняется.")
            return
        }

        if (!file.exists() || !file.canRead()) {
            app.err("Файл не найден или недоступен для чтения: ${file.absolutePath}")
            return
        }

        val input: InputProvider
        try {
            input = InputProvider.FileInputProvider(file)
        } catch (e: Exception) {
            handleError(e, app, file, 0)
            return
        }

        app.inputStack.push(input)
        app.fileStack.push(file)

        try {
            while (input.hasNextLine()) {
                val line = input.readLine()
                app.commandExecutor.handleCommand(line)
            }
        } catch (e: Exception) {
            handleError(e, app, file, input.lineNumber())
        } finally {
            app.fileStack.pop()
            app.inputStack.pop()
        }
    }

    private fun handleError(e: Throwable, app: ClientApp, file: File, lineNum: Int) {
        val isNested = app.fileStack.size > 1

        val scriptException = if (e is ScriptInputException) {
            ScriptInputException(
                file,
                lineNum,
                "Ошибка в скрипте '${file.name}'#$lineNum",
                e
            )
        } else {
            ScriptInputException(
                file,
                lineNum,
                "Ошибка исполнения в '${file.name}'#$lineNum: ${e.message}",
                e
            )
        }

        if (isNested) {
            throw scriptException
        } else {
            printScriptStackTrace(app, scriptException)
        }
    }

    private fun printScriptStackTrace(app: ClientApp, e: ScriptInputException) {
        app.userOutput().println("\n= ОШИБКА ИСПОЛНЕНИЯ СКРИПТА =")
        var current: Throwable? = e
        var depth = 0

        while (current != null) {
            val tab = "  ".repeat(depth)
            if (current is ScriptInputException) {
                app.userOutput().println("$tab-> ${current.message}")
            } else {
                app.userOutput().println("$tab-> Системная ошибка: ${current.javaClass.simpleName}: ${current.message}")
            }
            current = current.cause
            depth++
        }
        app.userOutput().println("=============================\n")
    }
}