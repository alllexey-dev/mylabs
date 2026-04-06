package dev.alllexey.common.io

import dev.alllexey.common.model.command.CommandArgumentMeta
import dev.alllexey.common.model.field.FieldType
import dev.alllexey.common.model.field.FieldType.Companion.verify
import dev.alllexey.common.protocol.Codec
import kotlinx.serialization.encodeToString

interface CommandHandler {

    val elementInputHelper: ElementInputHelper

    fun executeCommand(name: String, args: List<String>)

    fun handleCommand(line: String, prefilledValues: Map<String, String> = mapOf()) {
        val name = line.split(" ").getOrNull(0)
        if (name?.isBlank() ?: true) return
        val args = getArgsForName(name)
        if (args == null) throw IllegalArgumentException("Команда не найдена")
        val inputInlineArgs = line.removePrefix(name).trim().split(" ").let {
            if (it.size == 1 && it[0].isBlank()) emptyList()
            else it
        }
        val inlineArgs = matchInlineArgs(inputInlineArgs, args)
        val prefilledArgs = matchPrefilledArgs(inputInlineArgs, args)
        if (needsPermissionCheck(name)) {
            checkPermission(name, inlineArgs)
        }
        val complexArgs = inputComplexArgs(args, prefilledArgs)
        executeCommand(name, inlineArgs + complexArgs)
    }

    fun matchInlineArgs(inlineArgs: List<String>, args: List<CommandArgumentMeta>): List<String> {
        val requiredInlineArgs = args.filterNot { it.type is FieldType.ObjectType }
        if (requiredInlineArgs.size > inlineArgs.size) throw IllegalArgumentException("Неверный ввод команды: недостаточно аргументов")
        requiredInlineArgs.forEachIndexed { i, required ->
            val provided = inlineArgs[i]
            val type = required.type
            if (type is FieldType.EnumType) {
                if (!type.entries.contains(provided)) throw IllegalArgumentException("Неверный ввод команды: enum не найден")
                return@forEachIndexed
            }

            if (!type.verify(provided)) throw IllegalArgumentException("Неверный ввод команды: неправильно введён аргумент")
        }
        return inlineArgs.take(requiredInlineArgs.size)
    }

    fun matchPrefilledArgs(inlineArgs: List<String>, args: List<CommandArgumentMeta>): Map<String, String> {
        val requiredInlineArgs = args.filterNot { it.type is FieldType.ObjectType }
        if (requiredInlineArgs.size > inlineArgs.size) throw IllegalArgumentException("Неверный ввод команды: недостаточно аргументов")
        return requiredInlineArgs.mapIndexedNotNull { i, required ->
            if (required.prefilledPath == null) return@mapIndexedNotNull null
            val provided = inlineArgs[i]
            val type = required.type
            if (type is FieldType.EnumType) {
                if (!type.entries.contains(provided)) throw IllegalArgumentException("Неверный ввод команды: enum не найден")
                return@mapIndexedNotNull null
            }

            if (!type.verify(provided)) throw IllegalArgumentException("Неверный ввод команды: неправильно введён аргумент")

            required.prefilledPath to provided
        }.associate { it }
    }

    fun inputComplexArgs(
        args: List<CommandArgumentMeta>,
        prefilledValues: Map<String, String> = emptyMap(),
    ): List<String> {
        val requiredComplexArgs = args.filter { it.type is FieldType.ObjectType }
        return requiredComplexArgs.map { it ->
            val type = it.type as FieldType.ObjectType
            Codec.json.encodeToString(elementInputHelper.inputElement(type.objectMeta, prefilledValues, it.name + "."))
        }
    }

    fun checkPermission(name: String, inlineArgs: List<String>)

    fun getArgsForName(name: String): List<CommandArgumentMeta>? // null means command not found

    fun needsPermissionCheck(name: String): Boolean

}