package dev.alllexey.server.commands.impl

import dev.alllexey.common.protocol.Response
import dev.alllexey.server.commands.CommandContext
import dev.alllexey.server.commands.ServerCommand
import dev.alllexey.server.field.ObjectElementBuilder

object ShowCommand : ServerCommand(
    "show",
    listOf(),
    "вывести в стандартный поток вывода все элементы коллекции в строковом представлении"
) {

    override fun execute(args: List<String>, context: CommandContext): Response {
        return Response.ElementsResponse(
            context.app.collectionWrapper.items()
                .sortedWith {o1, o2 -> o1.compareTo(o2.name)}
                .map { ObjectElementBuilder.fromObject(it) }
        )
    }
}