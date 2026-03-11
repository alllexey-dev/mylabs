package dev.alllexey.common.protocol

import dev.alllexey.common.model.command.CommandMeta
import dev.alllexey.common.model.field.Element
import dev.alllexey.common.model.field.ObjectMeta
import kotlinx.serialization.Serializable

@Serializable
sealed class Response {

    @Serializable
    data class OkResponse(val message: String) : Response()

    @Serializable
    data class IncorrectInputResponse(val message: String) : Response()

    @Serializable
    data class ErrorResponse(val message: String) : Response()

    @Serializable
    data class ElementResponse(val element: Element) : Response()

    @Serializable
    data class ElementsResponse(val elements: List<Element>) : Response()

    @Serializable
    data class AllCommandsResponse(val commands: List<CommandMeta>): Response()
}