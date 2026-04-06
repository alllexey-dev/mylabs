package dev.alllexey.server.user

data class User(
    var id: Long = -1,
    val login: String,
    val passwordHash: String
)