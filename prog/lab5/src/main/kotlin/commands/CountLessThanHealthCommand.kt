package dev.alllexey.commands

import dev.alllexey.App

object CountLessThanHealthCommand : Command(
    "count_less_than_health",
    "count_less_than_health [health]",
    "вывести количество элементов, значение поля health которых меньше заданного"
) {

    override fun execute(args: List<String>, context: App) {
        val healthStr = args.getOrNull(0)
        if (healthStr == null || healthStr.isBlank()) {
            context.err("Значение здоровья не может быть пустым!")
            return
        }

        val health = healthStr.toDoubleOrNull()
        if (health == null) {
            context.err("Значение здоровья введено неверно!")
            return
        }

        if (!context.isReadingFromFile()) {
            context.collectionWrapper.items().count { item -> item.health < health }
        }
    }
}