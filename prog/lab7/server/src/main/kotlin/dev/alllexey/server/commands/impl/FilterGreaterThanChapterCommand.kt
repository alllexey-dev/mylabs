package dev.alllexey.server.commands.impl

import dev.alllexey.common.model.command.arg
import dev.alllexey.common.model.field.FieldType
import dev.alllexey.common.protocol.Response
import dev.alllexey.server.commands.CommandContext
import dev.alllexey.server.commands.ServerCommand
import dev.alllexey.server.field.ObjectElementBuilder

object FilterGreaterThanChapterCommand : ServerCommand(
    "filter_greater_than_chapter",
    listOf(arg { name = "chapter_name"; type = FieldType.StringType }),
    "вывести элементы, значение поля chapter которых больше заданного"
) {

    override fun execute(args: List<String>, context: CommandContext): Response {
        val chapterName = args.getOrNull(0)
        if (chapterName.isNullOrBlank()) {
            return Response.IncorrectInputResponse("Имя части не может быть пустым!")
        }

        return Response.ElementsResponse(
            context.app.collectionManager.items().filter { item -> item.chapter.name > chapterName }
                .map { ObjectElementBuilder.fromObject(it) }
        )
    }
}