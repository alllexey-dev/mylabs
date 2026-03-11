package dev.alllexey.client.command

import dev.alllexey.client.ClientApp
import dev.alllexey.common.io.CommandHandler
import dev.alllexey.common.io.ElementInputHelper
import dev.alllexey.common.model.command.CommandArgumentMeta
import dev.alllexey.common.model.command.CommandMeta
import dev.alllexey.common.protocol.Request

class CommandExecutor(
    val app: ClientApp,
    val localCommands: HashMap<String, ClientCommand> = linkedMapOf(),
    val serverCommands: HashMap<String, CommandMeta> = linkedMapOf(),
    override val elementInputHelper: ElementInputHelper = ElementInputHelper({ app.input() }, { app.output() }),
) : CommandHandler {

    override fun executeCommand(name: String, args: List<String>) {
        val localCommand = localCommands[name]
        val serverCommand = serverCommands[name]
        if (localCommand != null) localCommand.execute(args, app)
        else if (serverCommand != null) app.nioClient.send(Request.ExecuteCommand(name, args))
    }

    override fun getArgsForName(name: String): List<CommandArgumentMeta>? {
        return localCommands[name]?.args ?: serverCommands[name]?.args
    }
}