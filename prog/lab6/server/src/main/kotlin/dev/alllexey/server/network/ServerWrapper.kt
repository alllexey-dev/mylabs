package dev.alllexey.server.network

import dev.alllexey.server.App
import java.lang.Exception
import java.net.ServerSocket
import java.util.logging.Logger

class ServerWrapper(
    app: App,
    val port: Int,
) {

    val logger: Logger = Logger.getLogger(ServerWrapper::class.qualifiedName)

    var isRunning: Boolean = false
        private set

    lateinit var server: ServerSocket
    lateinit var serverThread: Thread
    val socketHandler = SocketHandler(app)

    fun start() {
        if (isRunning) throw IllegalStateException("Already running")
        isRunning = true
        server = ServerSocket(port)
        logger.info("Server started! Listening on ${server.localPort}")
        serverThread = Thread {
            while (true) {
                try {
                    if (!isRunning) break
                    val socket = server.accept()
                    logger.info("Server accepted new client connection!")
                    socketHandler.handle(socket)
//                    Thread { socketHandler.handle(socket) }.start() // multithreaded
                } catch (_: InterruptedException) {
                    logger.info("Server thread stopped!")
                    break
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        serverThread.start()
    }

    fun stop() {
        if (!isRunning) throw IllegalStateException("Already stopped")
        isRunning = false
        serverThread.interrupt()
        server.close()
        logger.info("Server stopped!")
    }
}