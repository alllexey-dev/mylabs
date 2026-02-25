package dev.alllexey.commands

import dev.alllexey.App
import dev.alllexey.model.SpaceMarine

object ReplaceIfLowerCommand : Command(
    "replace_if_lower",
    "replace_if_lower [id] {element}",
    "заменить значение по ключу, если новое значение меньше старого"
) {

    var nameArg: String? = null

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

        val marine = context.collectionWrapper.getItemById(id)
        if (marine == null) {
            context.err("Корабль с ID '$id' не найден!")
            return
        }

        nameArg = marine.name

        val obj = context.objectInputHandler.inputObject(SpaceMarine::class)
        if (obj.name < nameArg!!) {
            context.collectionWrapper.addItem(obj)
        }
    }
}