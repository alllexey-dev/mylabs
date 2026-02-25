package dev.alllexey.commands

import dev.alllexey.App

object HelpCommand : Command(
    "help",
    "help",
    "вывести справку по доступным командам"
) {

    override fun execute(args: List<String>, context: App) {
        if (!context.isReadingFromFile()) {
            context.scriptExecutor.commands.forEach { name, command ->
                println("${command.usage} : ${command.description}")
            }
        }
    }
}