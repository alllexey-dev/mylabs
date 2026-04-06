package dev.alllexey.server.commands.impl

import dev.alllexey.common.protocol.Response
import dev.alllexey.server.commands.CommandContext
import dev.alllexey.server.commands.ServerCommand

object HistoryCommand : ServerCommand(
    "history",
    listOf(),
    "вывести последние 14 команд (без их аргументов)"
) {

    override fun execute(args: List<String>, context: CommandContext): Response {
        return Response.OkResponse(context.app.commandExecutor.history.takeLast(14).joinToString(" ") { it })
    }
}