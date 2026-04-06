package dev.alllexey.server.data

import java.sql.Connection
import java.sql.DriverManager

class Database(
    val url: String,
    val username: String,
    val password: String,
) {

    val connection: Connection
        get() = DriverManager.getConnection(url, username, password)

}