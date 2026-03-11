package dev.alllexey.server.commands.impl

import dev.alllexey.common.protocol.Response
import dev.alllexey.server.commands.CommandContext
import dev.alllexey.server.commands.ServerCommand
import java.io.File

object SaveCommand : ServerCommand(
    "save",
    listOf(),
    "сохранить коллекцию в файл",
    serversideOnly = true
) {

    override fun execute(args: List<String>, context: CommandContext): Response {
        val filename = context.app.filenameProvider.invoke()
        val items = context.app.collectionWrapper.items()
        val content = context.app.serializer.serializeCollection(items)
        context.app.fileWriter.write(File(filename), content)
        return Response.OkResponse("Файл сохранён")
    }
}