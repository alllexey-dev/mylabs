package dev.alllexey.common.protocol

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Codec {

    val json = Json {
        encodeDefaults = true
        classDiscriminator = "_type"
    }

    fun encodeRequest(request: Request): String {
        return json.encodeToString(request)
    }

    fun decodeRequest(str: String): Request {
        return json.decodeFromString(str.trim())
    }

    fun encodeResponse(response: Response): String {
        return json.encodeToString(response)
    }

    fun decodeResponse(str: String): Response {
        return json.decodeFromString(str.trim())
    }

    fun ByteArray.toHex(): String =
        joinToString("") { "%02x".format(it) }
}