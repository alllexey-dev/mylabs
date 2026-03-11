package dev.alllexey.client.command.impl

import dev.alllexey.client.ClientApp
import dev.alllexey.client.command.ClientCommand
import kotlin.system.exitProcess

object ExitCommand : ClientCommand(
    "exit",
    listOf(),
    "завершить программу (без сохранения в файл)",
) {
    override fun execute(args: List<String>, app: ClientApp) {
        app.userOutput().println("Завершение работы клиента...")
        exitProcess(0)
    }
}