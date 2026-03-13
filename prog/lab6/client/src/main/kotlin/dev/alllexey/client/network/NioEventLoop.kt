package dev.alllexey.client.network

import dev.alllexey.common.protocol.Request
import dev.alllexey.common.protocol.Response
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.channels.*
import java.util.logging.Logger
import kotlin.concurrent.thread

class NioEventLoop(
    private val host: String,
    private val port: Int
) {

    val logger: Logger = Logger.getLogger(NioEventLoop::class.java.canonicalName)

    private val selector = Selector.open()

    @Volatile
    private var connection: ClientConnection? = null

    @Volatile
    private var isRunning = false

    init {
        connect()
    }

    private fun connect() {
        try {
            val channel = SocketChannel.open()
            channel.configureBlocking(false)
            channel.connect(InetSocketAddress(host, port))
            channel.register(selector, SelectionKey.OP_CONNECT)

            connection = ClientConnection(channel)
        } catch (e: Exception) {
            logger.warning("Failed to initiate connection: ${e.message}")
            scheduleReconnect()
        }
    }

    fun start(onResponse: (Response) -> Unit) {
        if (isRunning) return
        isRunning = true

        thread(name = "NioThread") {
            while (isRunning) {
                try {
                    selector.select()

                    val keys = selector.selectedKeys().iterator()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        keys.remove()

                        if (!key.isValid) continue

                        try {
                            when {
                                key.isConnectable -> handleConnect(key)
                                key.isReadable -> handleRead(onResponse)
                                key.isWritable -> handleWrite(key)
                            }
                        } catch (e: IOException) {
                            logger.warning("Connection error: ${e.message}")
                            handleDisconnect(key)
                        } catch (e: CancelledKeyException) {
                            handleDisconnect(key)
                        }
                    }
                } catch (e: Exception) {
                    logger.warning("Event loop error: ${e.message}")
                }
            }
        }
    }

    fun send(request: Request) {
        val currentConnection = connection
        if (currentConnection == null) {
            logger.warning("Client disconnected. Dropping request.")
            return
        }

        try {
            currentConnection.send(request)
            val key = currentConnection.channel.keyFor(selector)

            if (key != null && key.isValid) {
                key.interestOps(key.interestOps() or SelectionKey.OP_WRITE)
                selector.wakeup()
            }
        } catch (e: Exception) {
            logger.warning("Failed to send request: ${e.message}")
        }
    }

    private fun handleConnect(key: SelectionKey) {
        val channel = key.channel() as SocketChannel
        if (channel.finishConnect()) {
            key.interestOps(SelectionKey.OP_READ)
            logger.info("Connected to server!")

            logger.info("Trying to request commands...")
            send(Request.GetAllServerCommands)
        }
    }

    private fun handleRead(onResponse: (Response) -> Unit) {
        val currentConnection = connection ?: return
        val responses = currentConnection.read()
        for (resp in responses) {
            onResponse(resp)
        }
    }

    private fun handleWrite(key: SelectionKey) {
        val currentConnection = connection ?: return

        currentConnection.writer.write(currentConnection.channel)
        if (!currentConnection.writer.hasPending()) {
            key.interestOps(key.interestOps() and SelectionKey.OP_WRITE.inv())
        }
    }

    private fun handleDisconnect(key: SelectionKey) {
        key.cancel()
        try {
            key.channel().close()
        } catch (e: Exception) {
        }
        scheduleReconnect()
    }

    private fun scheduleReconnect() {
        connection = null
        logger.warning("Reconnecting in 3 seconds...")
        Thread.sleep(3000)

        connect()
    }
}