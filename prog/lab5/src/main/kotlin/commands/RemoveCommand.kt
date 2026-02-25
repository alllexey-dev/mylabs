package dev.alllexey.commands

import dev.alllexey.App

object RemoveCommand : Command(
    "remove",
    "remove [id]",
    "удалить элемент из коллекции по его ключу"
) {

    override fun execute(args: List<String>, context: App) {
        val idStr = args.getOrNull(0)
        if (idStr == null || idStr.isBlank()) {
            context.err("ID корабля не может быть пустым!")
            return
        }

        val id = idStr.toLongOrNull()
        if (id == null) {
            context.err("ID корабля введен неверно!")
            return
        }

        context.collectionWrapper.removeItem(id)
    }
}