package dev.alllexey.commands

import dev.alllexey.App

object ShowCommand : Command(
    "show",
    "show",
    "вывести в стандартный поток вывода все элементы коллекции в строковом представлении"
) {

    override fun execute(args: List<String>, context: App) {
        context.collectionWrapper.items().forEach {
            println(it)
        }
    }
}