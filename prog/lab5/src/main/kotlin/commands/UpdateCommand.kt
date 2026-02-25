package dev.alllexey.commands

import dev.alllexey.App
import dev.alllexey.model.SpaceMarine

object UpdateCommand : Command(
    "update",
    "update [id] {element}",
    "обновить значение элемента коллекции, id которого равен заданному"
) {

    var nameArg: String? = null
    var id: Long? = null

    override fun execute(args: List<String>, context: App) {
        val idStr = args.getOrNull(0)
        if (idStr == null || idStr.isBlank()) {
            context.err("ID корабля не может быть пустым!")
            return
        }

        id = idStr.toLongOrNull()
        if (id == null) {
            context.err("ID корабля введен неверно!")
            return
        }

        val marine = context.collectionWrapper.getItemById(id!!)
        if (marine == null) {
            context.err("Корабль с ID '$id' не найден!")
            return
        }

        nameArg = marine.name

        val obj = context.objectInputHandler.inputObject(SpaceMarine::class)
        context.collectionWrapper.addItem(obj)
    }
}