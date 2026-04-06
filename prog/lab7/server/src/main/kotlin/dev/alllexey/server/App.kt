package dev.alllexey.server

import dev.alllexey.server.commands.CommandExecutor
import dev.alllexey.server.commands.impl.*
import dev.alllexey.server.data.Database
import dev.alllexey.server.data.DatabaseCollectionProxy
import dev.alllexey.server.data.HashMapCollectionManager
import dev.alllexey.server.model.SpaceMarine
import dev.alllexey.server.network.NioServer
import dev.alllexey.server.network.RequestHandler
import dev.alllexey.server.user.UserService
import java.util.*
import java.util.logging.Logger
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class App(
    val database: Database,
    serverPort: Int,
) {

    val logger: Logger = Logger.getLogger(App::class.qualifiedName)

    val server = NioServer(this, serverPort)
    val requestHandler = RequestHandler(this)
    val commandExecutor = CommandExecutor(this)
    val userService = UserService(this)

    val collectionManager = DatabaseCollectionProxy(
        app = this,
        type = SpaceMarine::class,
        runtimeManager = HashMapCollectionManager(this, SpaceMarine::class),
        tableName = "items"
    )

    init {
        val commands = listOf(
            InfoCommand, ShowCommand,
            InsertCommand, UpdateCommand, RemoveCommand,
            ClearCommand, HistoryCommand,
            ReplaceIfLowerCommand, RemoveLowerKeyCommand, CountLessThanHealthCommand,
            FilterLessThanChapterCommand, FilterGreaterThanChapterCommand
        )

        commandExecutor.commands.putAll(commands.associateBy { it.name })
    }

    fun start() {
        userService.createUsersTable()
        collectionManager.createTable()
        collectionManager.loadAll()
        thread(name = "NioServerThread") {
            server.start()
        }

        val scanner = Scanner(System.`in`)
        while (scanner.hasNextLine()) {
            val command = scanner.nextLine()
            if (command.startsWith("exit")) {
                exitProcess(0)
            } else {
                println("Неизвестная команда")
            }
        }
    }
}