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

object ReplaceIfLowerCommand : ServerCommand(
    "replace_if_lower",
    listOf(
        arg { name = "id"; type = FieldType.LongType },
        arg { name = "element"; type = FieldType.ObjectType(ObjectMetaMapper.buildMeta(SpaceMarine::class)) },
    ),
    "заменить значение по ключу, если новое значение меньше старого"
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

        val marine = context.app.collectionWrapper.getItemById(id)
        if (marine == null) {
            return Response.IncorrectInputResponse("Корабль с ID '$id' не найден!")
        }

        val elementArg = args.getOrNull(1)
        if (elementArg == null || elementArg.isBlank()) {
            return Response.IncorrectInputResponse("Элемент не может быть пустым!")
        }

        val element = Codec.json.decodeFromString<Element.ObjectElement>(elementArg)

        val ctx = GenerationContext(
            context.app,
            mapOf("id" to id)
        )

        val name = marine.name
        val obj = ObjectGenerator.generate<SpaceMarine>(element, ctx)

        if (obj.name < name) {
            context.app.collectionWrapper.addItem(obj)
            return Response.OkResponse("Элемент заменён")
        } else {
            return Response.OkResponse("Элемент не заменён")
        }
    }
}