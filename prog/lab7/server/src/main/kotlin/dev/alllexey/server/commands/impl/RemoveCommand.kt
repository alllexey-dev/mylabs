package dev.alllexey.server.commands.impl

import dev.alllexey.common.model.command.arg
import dev.alllexey.common.model.field.FieldType
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
        if (idStr.isNullOrBlank()) {
            return Response.IncorrectInputResponse("ID корабля не может быть пустым!")
        }

        val id = idStr.toLongOrNull() ?: return Response.IncorrectInputResponse("ID корабля введен неверно!")

        if (!context.app.collectionManager.containsItemById(id)) {
            return Response.IncorrectInputResponse("Корабль с ID '$id' не найден!")
        }

        try {
            context.app.collectionManager.removeItem(id, context.user)
        } catch (_: IllegalAccessException) {
            return Response.ErrorResponse("Вы не можете удалить этот элемент")
        }
        return Response.OkResponse("Элемент с ID $id удалён")
    }
}