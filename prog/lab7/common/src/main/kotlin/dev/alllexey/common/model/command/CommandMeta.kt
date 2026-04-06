package dev.alllexey.common.model.command

import kotlinx.serialization.Serializable

@Serializable
data class CommandMeta(
    val name: String,
    val args: List<CommandArgumentMeta>,
    val description: String,
    val checkPermission: Boolean = false
)