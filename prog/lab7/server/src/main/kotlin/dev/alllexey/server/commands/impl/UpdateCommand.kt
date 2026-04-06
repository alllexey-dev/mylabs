package dev.alllexey.server.commands.impl

import dev.alllexey.common.model.command.arg
import dev.alllexey.common.model.field.Element
import dev.alllexey.common.model.field.FieldType
import dev.alllexey.common.protocol.Codec
import dev.alllexey.common.protocol.Response
import dev.alllexey.server.commands.CommandContext
import dev.alllexey.server.commands.ServerCommand
import dev.alllexey.server.field.GenerationContext
import dev.alllexey.server.field.ObjectGenerator
import dev.alllexey.server.field.ObjectMetaMapper
import dev.alllexey.server.model.SpaceMarine

object UpdateCommand : ServerCommand(
    "update",
    listOf(
        arg { name = "id"; type = FieldType.LongType },
        arg { name = "element"; type = FieldType.ObjectType(ObjectMetaMapper.buildMeta(SpaceMarine::class)) },
    ),
    "обновить значение элемента коллекции, id которого равен заданному",
    checkPermission = true
) {

    override fun execute(args: List<String>, context: CommandContext): Response {
        val idStr = args.getOrNull(0)
        if (idStr.isNullOrBlank()) {
            return Response.IncorrectInputResponse("ID корабля не может быть пустым!")
        }

        val id = idStr.toLongOrNull() ?: return Response.IncorrectInputResponse("ID корабля введен неверно!")

        val marine = context.app.collectionManager.getItemById(id)
            ?: return Response.IncorrectInputResponse("Корабль с ID '$id' не найден!")

        val elementArg = args.getOrNull(1)
        if (elementArg.isNullOrBlank()) {
            return Response.IncorrectInputResponse("Элемент не может быть пустым!")
        }

        val element = Codec.json.decodeFromString<Element.ObjectElement>(elementArg)

        val ctx = GenerationContext(
            mapOf("id" to id),
            context.user
        )

        val obj = ObjectGenerator.generate(element, ctx, SpaceMarine::class)

        try {
            context.app.collectionManager.modifyItem(obj, context.user)
        } catch (_: IllegalAccessException) {
            return Response.ErrorResponse("Вы не можете изменить этот элемент")
        }
        return Response.OkResponse("Элемент изменён")
    }

    override fun checkPermission(args: List<String>, context: CommandContext): Response.PermissionResponse {
        val idStr = args.getOrNull(0)
        if (idStr.isNullOrBlank()) {
            return Response.PermissionResponse(false, "ID корабля не может быть пустым!")
        }

        val id = idStr.toLongOrNull() ?: return Response.PermissionResponse(false, "ID корабля введен неверно!")

        val marine = context.app.collectionManager.getItemById(id)
            ?: return Response.PermissionResponse(false, "Корабль с ID '$id' не найден!")

        if (marine.creatorId != context.user.id) {
            return Response.PermissionResponse(false, "Вы не можете изменить этот элемент")
        }

        return Response.PermissionResponse(true)
    }
}