package dev.alllexey.server.commands.impl

import dev.alllexey.common.protocol.Response
import dev.alllexey.server.commands.CommandContext
import dev.alllexey.server.commands.ServerCommand
import kotlin.system.exitProcess

object ExitCommand : ServerCommand(
    "exit",
    listOf(),
    "завершить программу (без сохранения в файл)",
    serversideOnly = true
) {

    override fun execute(args: List<String>, context: CommandContext): Response {
        exitProcess(0)
    }
}