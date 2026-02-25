package dev.alllexey.commands

import dev.alllexey.App

object InfoCommand : Command(
    "info",
    "info",
    "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)"
) {

    override fun execute(args: List<String>, context: App) {
        println("Тип коллекции: ${context.collectionWrapper.type()}")
        println("Дата создания: ${context.collectionWrapper.initializationDate()}")
        println("Количество элементов: ${context.collectionWrapper.size()}")
    }
}