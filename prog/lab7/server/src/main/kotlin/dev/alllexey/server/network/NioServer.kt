package dev.alllexey.server.network

import dev.alllexey.common.protocol.Codec
import dev.alllexey.server.App
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.*
import java.nio.charset.Charset
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.logging.Logger

class NioServer(
    private val app: App,
    private val port: Int
) {
    private val logger: Logger = Logger.getLogger(NioServer::class.qualifiedName)

    private lateinit var selector: Selector
    private lateinit var serverChannel: ServerSocketChannel

    @Volatile
    private var running = false

    private val processExecutor: ExecutorService = Executors.newCachedThreadPool { r ->
        Thread(r, "nio-process-${Thread.activeCount()}").apply { isDaemon = true }
    }

    private val sendExecutor: ExecutorService = Executors.newCachedThreadPool { r ->
        Thread(r, "nio-send-${Thread.activeCount()}").apply { isDaemon = true }
    }

    private val clientBuffers = ConcurrentHashMap<SocketChannel, StringBuilder>()

    fun start() {
        if (running) throw IllegalStateException("Server already running")
        running = true

        selector = Selector.open()
        serverChannel = ServerSocketChannel.open().apply {
            bind(InetSocketAddress(port))
            configureBlocking(false)
            register(selector, SelectionKey.OP_ACCEPT)
        }

        logger.info("Server started on port $port")

        try {
            while (running) {
                selector.select()

                val keys = selector.selectedKeys().iterator()
                while (keys.hasNext()) {
                    val key = keys.next()
                    keys.remove()

                    if (!key.isValid) continue

                    try {
                        when {
                            key.isAcceptable -> handleAccept(key)
                            key.isReadable -> handleRead(key)
                        }
                    } catch (e: Exception) {
                        logger.warning("Error handling key: ${e.message}")
                        closeClient(key)
                    }
                }
            }
        } finally {
            shutdown()
        }
    }

    fun stop() {
        running = false
        if (::selector.isInitialized) {
            selector.wakeup()
        }
    }

    private fun handleAccept(key: SelectionKey) {
        val server = key.channel() as ServerSocketChannel
        val client = server.accept() ?: return

        client.configureBlocking(false)
        client.register(selector, SelectionKey.OP_READ)

        clientBuffers[client] = StringBuilder()

        logger.info("Accepted new connection: $client")
    }

    private fun handleRead(key: SelectionKey) {
        val client = key.channel() as? SocketChannel ?: return

        if (!client.isOpen) {
            closeClient(client, key)
            return
        }

        val buffer = ByteBuffer.allocate(1024)
        val charset = Charset.defaultCharset()

        val linesToProcess = mutableListOf<String>()

        try {
            val bytesRead = client.read(buffer)

            if (bytesRead == -1) {
                logger.info("Client disconnected: $client")
                closeClient(client, key)
                return
            }

            if (bytesRead == 0) return

            buffer.flip()
            val text = charset.decode(buffer).toString()

            val sb = clientBuffers.computeIfAbsent(client) { StringBuilder() }
            sb.append(text)

            while (true) {
                val lineEnd = sb.indexOf("\n")
                if (lineEnd < 0) break

                val line = sb.substring(0, lineEnd).trim()
                sb.delete(0, lineEnd + 1)

                if (line.isNotEmpty()) {
                    linesToProcess += line
                }
            }

        } catch (e: ClosedChannelException) {
            closeClient(client, key)
            return
        } catch (e: Exception) {
            logger.warning("Read error: ${e.message}")
            closeClient(client, key)
            return
        }

        for (line in linesToProcess) {
            processExecutor.execute {
                handleRequest(client, line)
            }
        }
    }

    private fun handleRequest(client: SocketChannel, line: String) {
        try {
            val request = Codec.decodeRequest(line)
            logger.info("Received request: $request")

            val response = app.requestHandler.handleRequest(request, client)
            val encoded = Codec.encodeResponse(response) + "\n"

            sendExecutor.execute {
                sendResponse(client, encoded)
            }

        } catch (e: Exception) {
            logger.warning("Error handling request: ${e.message}")
        }
    }

    private fun sendResponse(client: SocketChannel, responseText: String) {
        val charset = Charset.defaultCharset()
        val buffer = charset.encode(responseText)

        try {
            synchronized(client) {
                while (buffer.hasRemaining()) {
                    val written = client.write(buffer)

                    if (written < 0) {
                        logger.info("Client disconnected while sending: $client")
                        closeClient(client)
                        return
                    }

                    if (written == 0) {
                        Thread.yield()
                    }
                }
            }
        } catch (e: ClosedChannelException) {
            closeClient(client)
        } catch (e: Exception) {
            logger.warning("Write error: ${e.message}")
            closeClient(client)
        }
    }

    private fun closeClient(key: SelectionKey) {
        val client = key.channel() as? SocketChannel
        if (client != null) {
            closeClient(client)
        }
        try {
            key.cancel()
        } catch (_: Exception) {}
    }

    private fun closeClient(client: SocketChannel, key: SelectionKey? = null) {
        try {
            clientBuffers.remove(client)
        } catch (_: Exception) {}

        try {
            key?.cancel()
        } catch (_: Exception) {}

        try {
            client.close()
        } catch (_: Exception) {}
    }

    private fun shutdown() {
        try {
            if (::serverChannel.isInitialized) serverChannel.close()
        } catch (_: Exception) {}

        try {
            if (::selector.isInitialized) selector.close()
        } catch (_: Exception) {}

        processExecutor.shutdownNow()
        sendExecutor.shutdownNow()

        logger.info("Server stopped")
    }
}