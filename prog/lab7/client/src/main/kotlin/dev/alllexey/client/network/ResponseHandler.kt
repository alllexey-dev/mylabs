package dev.alllexey.client.network

import dev.alllexey.client.ClientApp
import dev.alllexey.common.protocol.Response
import java.util.logging.Logger

class ResponseHandler(
    val app: ClientApp
) {

    val logger: Logger = Logger.getLogger(ClientApp::class.qualifiedName)

    fun handle(response: Response) {
        when (response) {
            is Response.CredentialsResponse -> {
                println("Успешная авторизация")
                println("Данные для входа сохранены")
                app.credentials = response.credentials
            }

            is Response.AllCommandsResponse -> app.commandExecutor.serverCommands.apply {
                val commands = response.commands
                logger.info("Received ${commands.size} server commands.")
                clear()
                putAll(commands.associateBy { it.name })
            }

            is Response.ElementResponse -> {
                app.userOutput().println(response.element)
            }

            is Response.ElementsResponse -> {
                for (elem in response.elements) {
                    app.userOutput().println(elem)
                }
            }
            is Response.ErrorResponse -> {
                app.userOutput().println("Сервер ответил ошибкой: " + response.message)
            }
            is Response.IncorrectInputResponse -> {
                app.userOutput().println("Сервер ответил ошибкой ввода: " + response.message)
            }
            is Response.OkResponse -> {
                app.userOutput().println(response.message)
            }

            is Response.PermissionResponse -> {
                app.commandExecutor.onPermissionResponse(response)
            }
        }
    }
}