package dev.alllexey.server.network

import dev.alllexey.common.protocol.Request
import dev.alllexey.common.protocol.Response
import dev.alllexey.server.App

class RequestHandler(
    val app: App,
) {

    fun handleRequest(req: Request): Response =
        when (req) {
            is Request.ExecuteCommand -> {
                app.commandExecutor.executeCommand(req.commandName, req.args)
            }
            Request.GetAllServerCommands -> {
                val metas = app.commandExecutor.commands.values.filter { !it.serversideOnly }.map { it.toMeta() }
                Response.AllCommandsResponse(metas)
            }
        }
    
}