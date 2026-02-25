package dev.alllexey.commands

import dev.alllexey.App

object ClearCommand : Command(
    "clear",
    "clear",
    "очистить коллекцию"
) {

    override fun execute(args: List<String>, context: App) {
        context.collectionWrapper.clear()
    }
}