package dev.alllexey.server.network

import dev.alllexey.common.protocol.Codec
import dev.alllexey.server.App
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.nio.charset.Charset
import java.util.logging.Logger

class NioServer(val app: App, val port: Int) {

    private val logger: Logger = Logger.getLogger(NioServer::class.qualifiedName)
    private lateinit var selector: Selector
    private lateinit var serverChannel: ServerSocketChannel
    private var running = false

    private val clientBuffers = mutableMapOf<SocketChannel, StringBuilder>()

    fun start() {
        if (running) throw IllegalStateException("Server already running")
        running = true

        selector = Selector.open()
        serverChannel = ServerSocketChannel.open()
        serverChannel.bind(InetSocketAddress(port))
        serverChannel.configureBlocking(false)
        serverChannel.register(selector, SelectionKey.OP_ACCEPT)

        logger.info("Server started on port $port")

        while (running) {
            selector.select()
            val keys = selector.selectedKeys().iterator()
            while (keys.hasNext()) {
                val key = keys.next()
                keys.remove()

                try {
                    when {
                        key.isAcceptable -> handleAccept(key)
                        key.isReadable -> handleRead(key)
                    }
                } catch (e: Exception) {
                    logger.warning("Error handling key: ${e.message}")
                    key.cancel()
                    (key.channel() as? SocketChannel)?.close()
                }
            }
        }

        serverChannel.close()
        selector.close()
        logger.info("Server stopped")
    }

    fun stop() {
        running = false
        selector.wakeup()
    }

    private fun handleAccept(key: SelectionKey) {
        val server = key.channel() as ServerSocketChannel
        val client = server.accept()
        client.configureBlocking(false)
        client.register(selector, SelectionKey.OP_READ)
        clientBuffers[client] = StringBuilder()
        logger.info("Accepted new connection: $client")
    }

    private fun handleRead(key: SelectionKey) {
        val client = key.channel() as SocketChannel
        val buffer = ByteBuffer.allocate(1024)
        val bytesRead = client.read(buffer)
        if (bytesRead == -1) {
            logger.info("Client disconnected: $client")
            clientBuffers.remove(client)
            client.close()
            key.cancel()
            return
        }

        buffer.flip()
        val text = Charset.defaultCharset().decode(buffer).toString()
        val sb = clientBuffers.getOrPut(client) { StringBuilder() }
        sb.append(text)

        var lineEnd: Int
        while (sb.indexOf("\n").also { lineEnd = it } >= 0) {
            val line = sb.substring(0, lineEnd).trim()
            sb.delete(0, lineEnd + 1)
            if (line.isEmpty()) continue

            try {
                val request = Codec.decodeRequest(line)
                logger.info("Received request: $request")
                val response = app.requestHandler.handleRequest(request)
                val responseBytes = Charset.defaultCharset().encode(Codec.encodeResponse(response) + "\n")
                client.write(responseBytes)
            } catch (e: Exception) {
                logger.warning("Error handling request: ${e.message}")
            }
        }
    }
}