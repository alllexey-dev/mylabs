package dev.alllexey.commands

import dev.alllexey.App
import java.io.File

object SaveCommand : Command(
    "save",
    "save",
    "сохранить коллекцию в файл"
) {

    override fun execute(args: List<String>, context: App) {
        val filename = context.filenameProvider.invoke()
        val items = context.collectionWrapper.items()
        val content = context.serializer.serializeCollection(items)
        context.fileWriter.write(File(filename), content)
    }
}