package dev.alllexey.server

import dev.alllexey.io.impl.CsvTool
import dev.alllexey.server.commands.CommandExecutor
import dev.alllexey.server.commands.impl.*
import dev.alllexey.server.data.HashMapCollectionWrapper
import dev.alllexey.server.io.impl.CustomFileReader
import dev.alllexey.server.io.impl.CustomFileWriter
import dev.alllexey.server.model.SpaceMarine
import dev.alllexey.server.network.NioServer
import dev.alllexey.server.network.RequestHandler
import java.io.File
import java.util.Scanner
import java.util.logging.Logger
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class App(
    val filenameProvider: () -> String,
    serverPort: Int,
) {

    val logger: Logger = Logger.getLogger(App::class.qualifiedName)

    val serverWrapper = NioServer(this, serverPort)
    val requestHandler = RequestHandler(this)
    val commandExecutor = CommandExecutor(this)

    val collectionWrapper = HashMapCollectionWrapper(this, SpaceMarine::class)

    val serializer = CsvTool(SpaceMarine.serializer())
    val fileReader = CustomFileReader()
    val fileWriter = CustomFileWriter()

    init {
        val commands = listOf(
            InfoCommand, ShowCommand,
            InsertCommand, UpdateCommand, RemoveCommand,
            ClearCommand, ExitCommand, HistoryCommand,
            ReplaceIfLowerCommand, RemoveLowerKeyCommand, CountLessThanHealthCommand,
            FilterLessThanChapterCommand, FilterGreaterThanChapterCommand, SaveCommand
        )

        commandExecutor.commands.putAll(commands.associateBy { it.name })
    }

    fun start() {
        loadCollection()
        thread(name = "NioServerThread") {
            serverWrapper.start()
        }

        val scanner = Scanner(System.`in`)

        try {
            while (scanner.hasNextLine()) {
                val command = scanner.nextLine()
                if (command.startsWith("save")) {
                    save()
                } else if (command.startsWith("exit")) {
                    save()
                    exitProcess(0)
                } else {
                    println("Неизвестная команда")
                }
            }
        } catch (e: InterruptedException) {
            save()
        }
    }

    fun save() {
        logger.info("Сохранение коллекции")
        val filename = filenameProvider.invoke()
        val items = collectionWrapper.items()
        val content = serializer.serializeCollection(items)
        fileWriter.write(File(filename), content)
    }

    private fun loadCollection() {
        val filename = filenameProvider.invoke()
        val content = fileReader.read(File(filename))
        if (content.isBlank()) return
        val collection = serializer.deserializeCollection(content, SpaceMarine::class)
        collection.forEach { collectionWrapper.addItem(it) }
    }
}