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

object InsertCommand : ServerCommand(
    "insert",
    listOf(
        arg { name = "name"; type = FieldType.StringType; prefilledPath = "element.name" },
        arg { name = "element"; type = FieldType.ObjectType(ObjectMetaMapper.buildMeta(SpaceMarine::class)) }
    ),
    "добавить новый элемент с заданным именем"
) {
    override fun execute(args: List<String>, context: CommandContext): Response {
        val nameArg = args.getOrNull(0)
        if (nameArg == null || nameArg.isBlank()) {
            return Response.IncorrectInputResponse("Название корабля не может быть пустым!")
        }

        val elementArg = args.getOrNull(1)
        if (elementArg == null || elementArg.isBlank()) {
            return Response.IncorrectInputResponse("Элемент не может быть пустым!")
        }

        val element = Codec.json.decodeFromString<Element.ObjectElement>(elementArg)

        val ctx = GenerationContext(
            context.app,
            mapOf("name" to nameArg)
        )

        val obj = ObjectGenerator.generate<SpaceMarine>(element, ctx)
        context.app.collectionWrapper.addItem(obj)
        return Response.OkResponse("Элемент добавлен в коллекцию")
    }
}