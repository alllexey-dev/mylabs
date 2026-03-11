package dev.alllexey.client.network

import dev.alllexey.common.protocol.Codec
import dev.alllexey.common.protocol.Request
import dev.alllexey.common.protocol.Response
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class ClientConnection(
    val channel: SocketChannel
) {

    private val readBuffer = ByteBuffer.allocate(4096)
    private val framer = PacketFramer()
    val writer = ChannelWriter()

    fun send(request: Request) {
        writer.enqueue((Codec.encodeRequest(request) + "\n").toByteArray())
    }

    fun read(): List<Response> {
        val responses = mutableListOf<Response>()

        val bytesRead = channel.read(readBuffer)
        if (bytesRead == -1) {
            throw IOException("Server closed connection.")
        }
        if (bytesRead <= 0) return responses
        readBuffer.flip()
        val str = Charsets.UTF_8.decode(readBuffer).toString()
        readBuffer.clear()
        val messages = framer.feed(str)
        for (msg in messages) {
            responses.add(Codec.decodeResponse(msg))
        }

        return responses
    }
}