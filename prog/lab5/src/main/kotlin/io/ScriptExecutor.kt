package dev.alllexey.io

import dev.alllexey.App
import dev.alllexey.commands.Command

class ScriptExecutor(
    val app: App,
    val commands: HashMap<String, Command> = linkedMapOf(),
) {

    var runningCommand: Command? = null
    private set

    val history: ArrayList<String> = arrayListOf()

    fun executeCommand(line: String) {
        val name = line.split(" ").getOrNull(0)
        if (name == null) return
        history.add(name)
        runningCommand = commands[name]
        if (runningCommand == null) {
            app.err("Команда $name не найдена!")
            return
        }

        runningCommand?.execute(line.removePrefix(name).trim().split(" "), app)
    }
}