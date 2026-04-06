package dev.alllexey.client

import dev.alllexey.common.LogUtil
import java.util.logging.Level

fun main() {
    LogUtil.configureLogging(Level.INFO)
    val app = ClientApp("localhost", 5000)
    app.start()
}