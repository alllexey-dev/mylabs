package dev.alllexey.server.commands.impl

import dev.alllexey.common.model.command.arg
import dev.alllexey.common.model.field.FieldType
import dev.alllexey.common.protocol.Request
import dev.alllexey.common.protocol.Response
import dev.alllexey.server.commands.CommandContext
import dev.alllexey.server.commands.ServerCommand

object RemoveCommand : ServerCommand(
    "remove",
    listOf(arg { name = "id"; type = FieldType.LongType }),
    "удалить элемент из коллекции по его ключу"
) {

    override fun execute(args: List<String>, context: CommandContext): Response {
        val idStr = args.getOrNull(0)
        if (idStr == null || idStr.isBlank()) {
            return Response.IncorrectInputResponse("ID корабля не может быть пустым!")
        }

        val id = idStr.toLongOrNull()
        if (id == null) {
            return Response.IncorrectInputResponse("ID корабля введен неверно!")
        }

        if (!context.app.collectionWrapper.containsItemById(id)) {
            return Response.IncorrectInputResponse("Корабль с ID '$id' не найден!")
        }

        context.app.collectionWrapper.removeItem(id)
        return Response.OkResponse("Элемент с ID $id удалён")
    }
}