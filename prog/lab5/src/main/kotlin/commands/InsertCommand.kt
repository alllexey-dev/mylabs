package dev.alllexey.commands

import dev.alllexey.App
import dev.alllexey.model.SpaceMarine

object InsertCommand : Command(
    "insert",
    "insert [name] {element}",
    "добавить новый элемент с заданным именем"
) {

    var nameArg: String? = null

    override fun execute(args: List<String>, context: App) {
        nameArg = args.getOrNull(0)
        if (nameArg == null || nameArg!!.isBlank()) {
            context.err("Название корабля не может быть пустым!")
            return
        }

        val obj = context.objectInputHandler.inputObject(SpaceMarine::class)
        context.collectionWrapper.addItem(obj)
    }
}