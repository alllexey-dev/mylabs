package dev.alllexey.client.command.impl

import dev.alllexey.client.ClientApp
import dev.alllexey.client.command.ClientCommand
import dev.alllexey.common.model.command.arg
import dev.alllexey.common.model.field.FieldType
import dev.alllexey.common.protocol.Codec.toHex
import dev.alllexey.common.protocol.Request
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object LoginCommand : ClientCommand(
    "login",
    listOf(arg { name = "login"; type = FieldType.StringType }, arg { name = "password"; type = FieldType.StringType }),
    "войти",
) {

    override fun execute(args: List<String>, app: ClientApp) {
        val login = args.getOrNull(0)
        val password = args.getOrNull(1)
        if (login.isNullOrBlank()) {
            app.userOutput().println("Логин не может быть пустым")
            return
        }

        if (password.isNullOrBlank()) {
            app.userOutput().println("Пароль не может быть пустым")
            return
        }

        val digest = MessageDigest.getInstance("SHA-512")
        val passwordHash = digest.digest(password.toByteArray(StandardCharsets.UTF_8))
        val request = Request.Login(Request.AuthData(login, passwordHash.toHex()))
        app.nioClient.send(request)
    }
}