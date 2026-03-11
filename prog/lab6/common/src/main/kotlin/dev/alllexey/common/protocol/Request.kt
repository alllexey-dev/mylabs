package dev.alllexey.common.protocol

import kotlinx.serialization.Serializable

@Serializable
sealed class Request {

    @Serializable object GetAllServerCommands : Request()

    @Serializable data class ExecuteCommand(val commandName: String, val args: List<String>) : Request()
}