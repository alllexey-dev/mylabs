package dev.alllexey.server

import dev.alllexey.common.LogUtil
import java.util.logging.Level

fun main() {
    LogUtil.configureLogging(Level.INFO)

    val filenameProvider = {
        System.getenv("LAB_FILENAME") ?: throw RuntimeException("Missing \"LAB_FILENAME\" env variable")
    }

    val app = App(
        filenameProvider,
        5000
    )

    app.start()
}


