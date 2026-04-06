package dev.alllexey.client.network

import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util.*

class ChannelWriter {

    private val queue: Queue<ByteBuffer> = LinkedList()

    fun enqueue(bytes: ByteArray) {
        queue.add(ByteBuffer.wrap(bytes))
    }

    fun write(channel: SocketChannel) {
        while (queue.isNotEmpty()) {
            val buf = queue.peek()
            channel.write(buf)
            if (buf.hasRemaining()) {
                return
            }

            queue.poll()
        }
    }

    fun hasPending(): Boolean {
        return queue.isNotEmpty()
    }
}