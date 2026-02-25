package dev.alllexey.commands

import dev.alllexey.App

object FilterLessThanChapterCommand : Command(
    "filter_less_than_chapter",
    "filter_less_than_chapter [chapter_name]",
    "вывести элементы, значение поля chapter которых меньше заданного"
) {

    override fun execute(args: List<String>, context: App) {
        val chapterName = args.getOrNull(0)
        if (chapterName == null || chapterName.isBlank()) {
            context.err("Имя части не может быть пустым!")
            return
        }

        if (!context.isReadingFromFile()) {
            context.collectionWrapper.items().filter { item -> item.chapter.name < chapterName }
                .forEach { println(it) }
        }
    }
}