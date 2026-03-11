package dev.alllexey.server.commands

import dev.alllexey.common.model.command.CommandArgumentMeta
import dev.alllexey.common.model.command.CommandMeta
import dev.alllexey.common.protocol.Response
import dev.alllexey.server.commands.CommandContext

abstract class ServerCommand(
    val name: String,
    val args: List<CommandArgumentMeta>,
    val description: String,
    val serversideOnly: Boolean = false
) {

    abstract fun execute(args: List<String>, context: CommandContext): Response

    fun toMeta(): CommandMeta {
        return CommandMeta(
            name = name,
            args = args,
            description = description
        )
    }
}