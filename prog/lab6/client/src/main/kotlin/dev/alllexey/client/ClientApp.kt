package dev.alllexey.client

import dev.alllexey.client.command.CommandExecutor
import dev.alllexey.client.command.impl.ExecuteScriptCommand
import dev.alllexey.client.command.impl.ExitCommand
import dev.alllexey.client.command.impl.HelpCommand
import dev.alllexey.client.exception.ScriptInputException
import dev.alllexey.common.io.InputProvider
import dev.alllexey.client.network.NioEventLoop
import dev.alllexey.client.network.ResponseHandler
import dev.alllexey.common.protocol.Request
import java.io.File
import java.io.PrintStream
import java.util.Scanner
import java.util.Stack
import java.util.logging.Logger

class ClientApp(
    host: String,
    port: Int,
) {

    val logger: Logger = Logger.getLogger(ClientApp::class.qualifiedName)

    val nioClient = NioEventLoop(host, port)
    val commandExecutor = CommandExecutor(this)
    val responseHandler = ResponseHandler(this)

    // user io
    var fileStack = Stack<File>()
    var inputStack: Stack<InputProvider> = Stack<InputProvider>().apply { add(InputProvider.CONSOLE) }

    var isStarted = false
    private set

    init {
        val clientCommands = listOf(
            HelpCommand, ExecuteScriptCommand, ExitCommand
        )

        commandExecutor.localCommands.putAll(clientCommands.associateBy { it.name })
    }

    fun start() {
        if (isStarted) throw IllegalStateException("Already started")
        isStarted = true

        logger.info("Trying to connect...")
        nioClient.start {
            responseHandler.handle(it)
        }

        val scanner = Scanner(System.`in`)

        while (scanner.hasNextLine()) {
            val command = scanner.nextLine()

            try {
                commandExecutor.handleCommand(command)
            } catch (e: Exception) {
                println("Ошибка при выполнении команды: ${e.message}")
            }
        }
    }

    fun isReadingFromFile(): Boolean {
        return fileStack.isNotEmpty()
    }

    fun err(message: String) {
        if (!isReadingFromFile()) {
            println(message)
            return
        }
        throw ScriptInputException(fileStack.peek(), input().lineNumber(), message)
    }

    fun input(): InputProvider {
        return inputStack.peek()
    }

    fun output(): PrintStream? {
        if (isReadingFromFile()) return null
        return System.out
    }

    fun userOutput(): PrintStream {
        return System.out
    }
}