package dev.alllexey.common

import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger

object LogUtil {

    fun configureLogging(level: Level) {
        val root: Logger = Logger.getLogger("")

        for (h in root.handlers) {
            root.removeHandler(h)
        }

        val console = ConsoleHandler()
        console.setLevel(level)

        root.addHandler(console)
        root.setLevel(level)
    }
}