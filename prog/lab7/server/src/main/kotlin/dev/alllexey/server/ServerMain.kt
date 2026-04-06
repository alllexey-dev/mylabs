package dev.alllexey.server

import dev.alllexey.common.LogUtil
import dev.alllexey.server.data.Database
import java.util.logging.Level

fun main() {
    LogUtil.configureLogging(Level.INFO)

    val db = Database(
        System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:3300/mydb",
        System.getenv("DB_USERNAME") ?: "postgres",
        System.getenv("DB_PASSWORD") ?: "password"
    )

    val app = App(
        database = db,
        serverPort = 5000
    )

    app.start()
}


