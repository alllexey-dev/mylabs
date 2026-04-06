package dev.alllexey.common.protocol

import kotlinx.serialization.Serializable

@Serializable
sealed class Request {

    @Serializable object GetAllServerCommands : Request()

    @Serializable data class ExecuteCommand(val commandName: String, val args: List<String>, val authData: AuthData) : Request()

    @Serializable data class CheckCommandPermissions(val commandName: String, val inlineArgs: List<String>, val authData: AuthData) : Request()

    @Serializable data class Login(val authData: AuthData) : Request()

    @Serializable data class Register(val authData: AuthData) : Request()

    @Serializable data class AuthData(val login: String, val passwordHash: String)
}