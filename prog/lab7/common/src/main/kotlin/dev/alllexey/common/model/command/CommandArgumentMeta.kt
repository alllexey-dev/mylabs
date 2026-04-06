package dev.alllexey.common.model.command

import dev.alllexey.common.model.field.FieldType
import kotlinx.serialization.Serializable

@Serializable
data class CommandArgumentMeta (
    val name: String,
    val type: FieldType,
    val prefilledPath: String? = null,
)

class CommandArgumentMetaBuilder {
    lateinit var name: String
    lateinit var type: FieldType
    var prefilledPath: String? = null

    fun build() = CommandArgumentMeta(name, type, prefilledPath)
}

fun arg(block: CommandArgumentMetaBuilder.() -> Unit): CommandArgumentMeta {
    return CommandArgumentMetaBuilder().apply(block).build()
}