package dev.alllexey.commands

import dev.alllexey.App
import kotlin.system.exitProcess

object ExitCommand : Command(
    "exit",
    "exit",
    "завершить программу (без сохранения в файл)"
) {

    override fun execute(args: List<String>, context: App) {
        exitProcess(0)
    }
}