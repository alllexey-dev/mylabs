package dev.alllexey.commands

import dev.alllexey.App

object HistoryCommand : Command(
    "history",
    "history",
    "вывести последние 14 команд (без их аргументов)"
) {

    override fun execute(args: List<String>, context: App) {
        if (!context.isReadingFromFile()) {
            context.scriptExecutor.history.takeLast(14).joinToString(" ") { it }
        }
    }
}