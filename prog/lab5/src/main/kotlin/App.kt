package dev.alllexey

import dev.alllexey.commands.ClearCommand
import dev.alllexey.commands.CountLessThanHealthCommand
import dev.alllexey.commands.ExecuteScriptCommand
import dev.alllexey.commands.ExitCommand
import dev.alllexey.commands.FilterGreaterThanChapterCommand
import dev.alllexey.commands.FilterLessThanChapterCommand
import dev.alllexey.commands.HelpCommand
import dev.alllexey.commands.HistoryCommand
import dev.alllexey.commands.InfoCommand
import dev.alllexey.commands.InsertCommand
import dev.alllexey.commands.RemoveCommand
import dev.alllexey.commands.RemoveLowerKeyCommand
import dev.alllexey.commands.ReplaceIfLowerCommand
import dev.alllexey.commands.SaveCommand
import dev.alllexey.commands.ShowCommand
import dev.alllexey.commands.UpdateCommand
import dev.alllexey.exceptions.ScriptInputException
import dev.alllexey.io.impl.CsvTool
import dev.alllexey.io.impl.CustomFileReader
import dev.alllexey.io.impl.CustomFileWriter
import dev.alllexey.io.InputProvider
import dev.alllexey.model.SpaceMarine
import dev.alllexey.io.ObjectInputHandler
import dev.alllexey.io.ScriptExecutor
import dev.alllexey.model.HashMapCollectionWrapper
import java.io.File
import java.util.*

class App(
    val filenameProvider: () -> String,
) {

    val scriptExecutor = ScriptExecutor(this)
    val collectionWrapper = HashMapCollectionWrapper(this, SpaceMarine::class)

    // console & script input
    val objectInputHandler = ObjectInputHandler(this)
    var fileStack = Stack<File>()
    var inputStack: Stack<InputProvider> = Stack<InputProvider>().apply { add(InputProvider.CONSOLE) }

    // io
    val serializer = CsvTool<SpaceMarine>()
    val fileWriter = CustomFileWriter()
    val fileReader = CustomFileReader()


    init {
        val commands = listOf(
            HelpCommand, InfoCommand, ShowCommand,
            InsertCommand, UpdateCommand, RemoveCommand,
            ClearCommand, ExitCommand, HistoryCommand,
            ReplaceIfLowerCommand, RemoveLowerKeyCommand, CountLessThanHealthCommand,
            FilterLessThanChapterCommand, FilterGreaterThanChapterCommand, ExecuteScriptCommand,
            SaveCommand
        )
        scriptExecutor.commands.putAll(commands.associateBy { it.name })
    }

    fun run() {
        loadCollection()

        val scanner = Scanner(System.`in`)
        if (!isReadingFromFile()) print("> ")

        while (scanner.hasNextLine()) {
            val command = scanner.nextLine()

            try {
                scriptExecutor.executeCommand(command)
            } catch (e: Exception) {
                println("Ошибка при выполнении команды: ${e.message}")
            }

            if (!isReadingFromFile()) print("> ")
        }
        println("EOF reached")
    }

    private fun loadCollection() {
        val filename = filenameProvider.invoke()
        val content = fileReader.read(File(filename))
        if (content.isBlank()) return
        val collection = serializer.deserializeCollection(content, SpaceMarine::class)
        collection.forEach { collectionWrapper.addItem(it) }
    }

    fun nextId(): Long {
        return (collectionWrapper.items().maxOfOrNull { it.id } ?: 0) + 1
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
}