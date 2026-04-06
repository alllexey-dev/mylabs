package dev.alllexey.server.commands.impl

import dev.alllexey.common.model.command.arg
import dev.alllexey.common.model.field.FieldType
import dev.alllexey.common.protocol.Response
import dev.alllexey.server.commands.CommandContext
import dev.alllexey.server.commands.ServerCommand

object RemoveLowerKeyCommand : ServerCommand(
    "remove_lower_key",
    listOf(arg { name = "id"; type = FieldType.LongType }),
    "удалить из коллекции все элементы, ключ которых меньше, чем заданный"
) {

    override fun execute(args: List<String>, context: CommandContext): Response {
        val idStr = args.getOrNull(0)
        if (idStr.isNullOrBlank()) {
            return Response.IncorrectInputResponse("ID корабля не может быть пустым!")
        }

        val id = idStr.toLongOrNull() ?: return Response.IncorrectInputResponse("ID корабля введен неверно!")

        context.app.collectionManager.items().filter { item -> item.id < id }.forEach {
            try {
                context.app.collectionManager.removeItem(it.id, context.user)
            } catch (_: IllegalAccessException) {
            }
        }

        return Response.OkResponse("Элементы удалены")
    }
}