package dev.alllexey.server.commands.impl

import dev.alllexey.common.protocol.Response
import dev.alllexey.server.commands.CommandContext
import dev.alllexey.server.commands.ServerCommand

object ClearCommand : ServerCommand(
    "clear",
    emptyList(),
    "очистить коллекцию"
) {

    override fun execute(args: List<String>, context: CommandContext): Response {
        context.app.collectionManager.clear(context.user)
        return Response.OkResponse("Коллекция очищена")
    }
}