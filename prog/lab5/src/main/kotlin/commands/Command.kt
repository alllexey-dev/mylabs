package dev.alllexey.commands

import dev.alllexey.App

abstract class Command(
    val name: String,
    val usage: String,
    val description: String,
) {

    abstract fun execute(args: List<String>, context: App)
}