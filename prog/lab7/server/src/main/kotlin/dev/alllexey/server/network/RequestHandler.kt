package dev.alllexey.server.network

import dev.alllexey.common.protocol.Request
import dev.alllexey.common.protocol.Response
import dev.alllexey.server.App
import java.nio.channels.SocketChannel

class RequestHandler(
    val app: App,
) {

    fun handleRequest(req: Request, client: SocketChannel): Response =
        when (req) {
            is Request.ExecuteCommand -> {
                val authData = req.authData
                val (user, response) = app.userService.handleLogin(authData.login, authData.passwordHash)
                if (user == null) {
                    response
                } else {
                    app.commandExecutor.executeCommand(req.commandName, req.args, user)
                }
            }

            Request.GetAllServerCommands -> {
                val metas = app.commandExecutor.commands.values.map { it.toMeta() }
                Response.AllCommandsResponse(metas)
            }

            is Request.Register -> {
                val authData = req.authData
                val (_, response) = app.userService.handleRegister(authData.login, authData.passwordHash)
                response
            }

            is Request.Login -> {
                val authData = req.authData
                val (_, response) = app.userService.handleLogin(authData.login, authData.passwordHash)
                response
            }

            is Request.CheckCommandPermissions -> {
                val authData = req.authData
                val (user, response) = app.userService.handleLogin(authData.login, authData.passwordHash)
                if (user == null) {
                    response
                } else {
                    app.commandExecutor.checkPermission(req.commandName, req.inlineArgs, user)
                }
            }
        }

}