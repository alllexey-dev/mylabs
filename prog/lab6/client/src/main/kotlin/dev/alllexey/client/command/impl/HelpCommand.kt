package dev.alllexey.client.command.impl

import dev.alllexey.client.ClientApp
import dev.alllexey.client.command.ClientCommand
import dev.alllexey.common.model.field.FieldType

object HelpCommand : ClientCommand(
    "help",
    listOf(),
    "вывести справку по доступным командам",
) {
    override fun execute(args: List<String>, app: ClientApp) {
        val executor = app.commandExecutor
        val metas = executor.localCommands.values.map { it.toMeta() } + executor.serverCommands.values
        metas.forEach { meta ->
            val args = meta.args.joinToString(" ") {
                if (it.type is FieldType.ObjectType) "{${it.name}}" else "[${it.name}]"
            }
            app.userOutput().println("${meta.name} ${args}: ${meta.description}")
        }
    }
}