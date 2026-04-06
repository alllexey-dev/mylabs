package dev.alllexey.client.command

import dev.alllexey.client.ClientApp
import dev.alllexey.common.model.command.CommandArgumentMeta
import dev.alllexey.common.model.command.CommandMeta

abstract class ClientCommand(
    val name: String,
    val args: List<CommandArgumentMeta>,
    val description: String,
) {

    abstract fun execute(args: List<String>, app: ClientApp)

    fun toMeta(): CommandMeta {
        return CommandMeta(
            name = name,
            args = args,
            description = description
        )
    }
}