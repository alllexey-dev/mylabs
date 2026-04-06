package dev.alllexey.server.commands

import dev.alllexey.common.model.command.CommandArgumentMeta
import dev.alllexey.common.model.command.CommandMeta
import dev.alllexey.common.protocol.Response
import dev.alllexey.server.commands.CommandContext

abstract class ServerCommand(
    val name: String,
    val args: List<CommandArgumentMeta>,
    val description: String,
    val checkPermission: Boolean = false,
) {

    abstract fun execute(args: List<String>, context: CommandContext): Response

    open fun checkPermission(args: List<String>, context: CommandContext): Response.PermissionResponse {
        return Response.PermissionResponse(true)
    }

    fun toMeta(): CommandMeta {
        return CommandMeta(
            name = name,
            args = args,
            description = description,
            checkPermission = checkPermission,
        )
    }
}