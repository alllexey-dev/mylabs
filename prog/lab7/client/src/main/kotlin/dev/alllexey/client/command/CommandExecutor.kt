package dev.alllexey.client.command

import dev.alllexey.client.ClientApp
import dev.alllexey.common.io.CommandHandler
import dev.alllexey.common.io.ElementInputHelper
import dev.alllexey.common.model.command.CommandArgumentMeta
import dev.alllexey.common.model.command.CommandMeta
import dev.alllexey.common.protocol.Request
import dev.alllexey.common.protocol.Response
import java.util.Objects
import java.util.concurrent.CompletableFuture

class CommandExecutor(
    val app: ClientApp,
    val localCommands: HashMap<String, ClientCommand> = linkedMapOf(),
    val serverCommands: HashMap<String, CommandMeta> = linkedMapOf(),
    override val elementInputHelper: ElementInputHelper = ElementInputHelper({ app.input() }, { app.output() }),
) : CommandHandler {

    private var permissionFuture: CompletableFuture<Response.PermissionResponse>? = null

    override fun executeCommand(name: String, args: List<String>) {
        val localCommand = localCommands[name]
        val serverCommand = serverCommands[name]
        if (localCommand != null) localCommand.execute(args, app)
        else if (serverCommand != null) {
            val credentials = app.credentials
            if (credentials == null) {
                println("Для выполнения серверных команд необходимо войти")
                println("Используйте login или register")
                return
            }
            app.nioClient.send(Request.ExecuteCommand(name, args, credentials))
        }
    }

    override fun getArgsForName(name: String): List<CommandArgumentMeta>? {
        return localCommands[name]?.args ?: serverCommands[name]?.args
    }

    override fun needsPermissionCheck(name: String): Boolean {
        return serverCommands[name]?.checkPermission ?: false
    }

    override fun checkPermission(name: String, inlineArgs: List<String>) {
        val credentials = app.credentials
            ?: throw IllegalArgumentException("Для выполнения команды необходимо войти")

        val future = CompletableFuture<Response.PermissionResponse>()
        permissionFuture = future

        app.nioClient.send(Request.CheckCommandPermissions(name, inlineArgs, credentials))

        val resp = future.get()

        if (!resp.allowed) {
            throw IllegalArgumentException(resp.message ?: "Нет прав")
        }
    }

    fun onPermissionResponse(resp: Response.PermissionResponse) {
        permissionFuture?.complete(resp)
    }
}