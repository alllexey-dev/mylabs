package dev.alllexey.server.commands

import dev.alllexey.common.protocol.Response
import dev.alllexey.server.App

data class CommandContext(
    val app: App,
) {
    fun err(message: String) {
        System.err.println(message)
    }
}