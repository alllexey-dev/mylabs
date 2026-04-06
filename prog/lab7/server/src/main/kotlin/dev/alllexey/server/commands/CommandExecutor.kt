package dev.alllexey.server.commands

import dev.alllexey.common.protocol.Response
import dev.alllexey.server.App
import dev.alllexey.server.user.User

class CommandExecutor(
    val app: App,
    val commands: HashMap<String, ServerCommand> = linkedMapOf(),
) {

    val history: ArrayList<String> = arrayListOf()

    fun executeCommand(name: String, args: List<String>, user: User): Response {
        val context = CommandContext(app, user)
        history.add(name)

        val command = commands[name]
        if (command == null) {
            return Response.ErrorResponse("Команда $name не найдена!")
        }

        return command.execute(args, context)
    }

    fun checkPermission(name: String, args: List<String>, user: User): Response.PermissionResponse {
        val context = CommandContext(app, user)
        val command = commands[name]
        if (command == null) {
            return Response.PermissionResponse(false, "Команда $name не найдена!")
        }

        return command.checkPermission(args, context)
    }
}