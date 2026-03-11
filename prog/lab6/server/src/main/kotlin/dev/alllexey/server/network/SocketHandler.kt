package dev.alllexey.server.network

import dev.alllexey.common.protocol.Codec
import dev.alllexey.server.App
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Logger

class SocketHandler(
    val app: App
) {
    val connNumber = AtomicInteger(0)
    val logger: Logger = Logger.getLogger(SocketHandler::class.qualifiedName)

    fun handle(socket: Socket) {
        val num = connNumber.incrementAndGet()
        val input = socket.getInputStream()
        val output = socket.getOutputStream()

        val reader = BufferedReader(InputStreamReader(input))
        val writer = BufferedWriter(OutputStreamWriter(output))

        while (true) {
            val line = reader.readLine() ?: break

            try {
                val request = Codec.decodeRequest(line)
                logger.info("New request from connection $num: $request")
                val response = app.requestHandler.handleRequest(request)
                logger.info("Sending response to connection $num: $response")
                writer.write(Codec.encodeResponse(response))
                writer.newLine()
            } catch (e: Exception) {
                logger.warning("Error while handling request: ${e.message}")
            }

            writer.flush()
        }

        logger.info("Closing connection $num")


        socket.close()
    }
}