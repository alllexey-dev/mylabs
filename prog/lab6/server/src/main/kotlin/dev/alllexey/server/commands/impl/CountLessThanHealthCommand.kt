package dev.alllexey.server.commands.impl

import dev.alllexey.common.model.command.arg
import dev.alllexey.common.model.field.FieldType
import dev.alllexey.common.protocol.Response
import dev.alllexey.server.commands.CommandContext
import dev.alllexey.server.commands.ServerCommand

object CountLessThanHealthCommand : ServerCommand(
    "count_less_than_health",
    listOf(arg { name = "health"; type = FieldType.DoubleType }),
    "вывести количество элементов, значение поля health которых меньше заданного"
) {

    override fun execute(args: List<String>, context: CommandContext): Response {
        val healthStr = args.getOrNull(0)
        if (healthStr == null || healthStr.isBlank()) {
            return Response.IncorrectInputResponse("Значение здоровья не может быть пустым!")
        }

        val health = healthStr.toDoubleOrNull()
        if (health == null) {
            return Response.IncorrectInputResponse("Значение здоровья введено неверно!")
        }

        return Response.OkResponse(context.app.collectionWrapper.items().count { it.health < health }.toString())
    }
}