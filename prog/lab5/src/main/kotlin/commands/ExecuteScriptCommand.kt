package dev.alllexey.commands

import dev.alllexey.App
import dev.alllexey.exceptions.ScriptInputException
import dev.alllexey.io.InputProvider
import java.io.File

object ExecuteScriptCommand : Command(
    "execute_script",
    "execute_script [file_name]",
    "считать и исполнить скрипт из указанного файла."
) {

    override fun execute(args: List<String>, context: App) {
        val filename = args.getOrNull(0)
        if (filename.isNullOrBlank()) {
            context.err("Название файла не может быть пустым!")
            return
        }

        val file = File(filename).absoluteFile

        if (context.fileStack.any { it.absolutePath == file.absolutePath }) {
            context.err("Обнаружена рекурсия. Скрипт ${file.name} уже выполняется.")
            return
        }

        if (!file.exists() || !file.canRead()) {
            context.err("Файл не найден или недоступен для чтения: ${file.absolutePath}")
            return
        }

        val input: InputProvider
        try {
            input = InputProvider.FileInputProvider(file)
        } catch (e: Exception) {
            handleError(e, context, file, 0)
            return
        }

        context.inputStack.push(input)
        context.fileStack.push(file)

        try {
            while (input.hasNextLine()) {
                val line = input.readLine()
                context.scriptExecutor.executeCommand(line)
            }
        } catch (e: Exception) {
            handleError(e, context, file, input.lineNumber())
        } finally {
            context.fileStack.pop()
            context.inputStack.pop()
        }
    }

    private fun handleError(e: Throwable, context: App, file: File, lineNum: Int) {
        val isNested = context.fileStack.size > 1

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
            printScriptStackTrace(scriptException)
        }
    }

    private fun printScriptStackTrace(e: ScriptInputException) {
        println("\n= ОШИБКА ИСПОЛНЕНИЯ СКРИПТА =")
        var current: Throwable? = e
        var depth = 0

        while (current != null) {
            val tab = "  ".repeat(depth)
            if (current is ScriptInputException) {
                println("$tab-> ${current.message}")
            } else {
                println("$tab-> Системная ошибка: ${current.javaClass.simpleName}: ${current.message}")
            }
            current = current.cause
            depth++
        }
        println("=============================\n")
    }
}