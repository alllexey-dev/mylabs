package dev.alllexey.server.commands.impl

import dev.alllexey.common.model.command.arg
import dev.alllexey.common.model.field.FieldType
import dev.alllexey.common.protocol.Response
import dev.alllexey.server.commands.CommandContext
import dev.alllexey.server.commands.ServerCommand
import dev.alllexey.server.field.ObjectElementBuilder

object FilterLessThanChapterCommand : ServerCommand(
    "filter_less_than_chapter",
    listOf(arg { name = "chapter_name"; type = FieldType.StringType }),
    "вывести элементы, значение поля chapter которых меньше заданного"
) {

    override fun execute(args: List<String>, context: CommandContext): Response {
        val chapterName = args.getOrNull(0)
        if (chapterName == null || chapterName.isBlank()) {
            return Response.IncorrectInputResponse("Имя части не может быть пустым!")
        }

        return Response.ElementsResponse(
            context.app.collectionWrapper.items().filter { item -> item.chapter.name < chapterName }
                .map { ObjectElementBuilder.fromObject(it) }
        )
    }
}