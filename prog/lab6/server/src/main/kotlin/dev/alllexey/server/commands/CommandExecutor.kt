package dev.alllexey.server.commands

import dev.alllexey.common.protocol.Response
import dev.alllexey.server.App

class CommandExecutor(
    val app: App,
    val commands: HashMap<String, ServerCommand> = linkedMapOf(),
) {

    val history: ArrayList<String> = arrayListOf()

    fun executeCommand(name: String, args: List<String>): Response {
        val context = CommandContext(app)
        history.add(name)

        val runningCommand = commands[name]
        if (runningCommand == null) {
            return Response.ErrorResponse("Команда $name не найдена!")
        }

        return runningCommand.execute(args, context)
    }
}