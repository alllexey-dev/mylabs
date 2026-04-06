package dev.alllexey.server.commands

import dev.alllexey.server.App
import dev.alllexey.server.user.User

data class CommandContext(
    val app: App,
    val user: User
)