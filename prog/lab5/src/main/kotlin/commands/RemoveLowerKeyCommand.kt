package dev.alllexey.commands

import dev.alllexey.App

object RemoveLowerKeyCommand : Command(
    "remove_lower_key",
    "remove_lower_key [id]",
    "удалить из коллекции все элементы, ключ которых меньше, чем заданный"
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

        context.collectionWrapper.items().filter { item -> item.id < id }.forEach {
            context.collectionWrapper.removeItem(it.id)
        }
    }
}