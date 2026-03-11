package dev.alllexey.server.commands.impl

import dev.alllexey.common.model.command.arg
import dev.alllexey.common.model.field.FieldType
import dev.alllexey.common.protocol.Response
import dev.alllexey.server.commands.CommandContext
import dev.alllexey.server.commands.ServerCommand
import dev.alllexey.server.field.ObjectMetaMapper
import dev.alllexey.server.model.SpaceMarine

object UpdateCommand : ServerCommand(
    "update",
    listOf(
        arg { name = "id"; type = FieldType.LongType },
        arg { name = "element"; type = FieldType.ObjectType(ObjectMetaMapper.buildMeta(SpaceMarine::class)) },
    ),
    "обновить значение элемента коллекции, id которого равен заданному"
) {

    var nameArg: String? = null
    var id: Long? = null

    override fun execute(args: List<String>, context: CommandContext): Response {
        val idStr = args.getOrNull(0)
        if (idStr == null || idStr.isBlank()) {
            return Response.IncorrectInputResponse("ID корабля не может быть пустым!")
        }

        id = idStr.toLongOrNull()
        if (id == null) {
            return Response.IncorrectInputResponse("ID корабля введен неверно!")
        }

        return Response.OkResponse("")

//        val marine = context.collectionWrapper.getItemById(id!!)
//        if (marine == null) {
//            context.err("Корабль с ID '$id' не найден!")
//            return
//        }
//
//        nameArg = marine.name
//
//        val obj = context.objectInputHandler.inputObject(SpaceMarine::class)
//        context.collectionWrapper.addItem(obj)
    }
}