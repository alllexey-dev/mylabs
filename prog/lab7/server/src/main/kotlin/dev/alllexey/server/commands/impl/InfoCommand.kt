package dev.alllexey.server.commands.impl

import dev.alllexey.common.protocol.Response
import dev.alllexey.server.commands.CommandContext
import dev.alllexey.server.commands.ServerCommand

object InfoCommand : ServerCommand(
    "info",
    listOf(),
    "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)"
) {

    override fun execute(args: List<String>, context: CommandContext): Response {
        val str = buildString {
            appendLine("Тип коллекции: ${context.app.collectionManager.type()}")
            append("Количество элементов: ${context.app.collectionManager.size()}")
        }
        return Response.OkResponse(str)
    }
}