package dev.alllexey.io

import dev.alllexey.App
import dev.alllexey.exceptions.NotGeneratedException
import dev.alllexey.exceptions.ScriptInputException
import dev.alllexey.reflect.FieldMeta
import dev.alllexey.reflect.FieldTools
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class ObjectInputHandler(
    val app: App,
) {

    private val tab = "  "

    fun <T : Any> inputObject(
        clazz: KClass<T>,
        depth: Int = 0,
    ): T {

        val metas = FieldTools.fieldMetas(clazz)
        val values = mutableMapOf<String, Any?>()

        for (meta in metas) {
            if (meta.generator != null) {
                try {
                    values[meta.name] = generateValue(meta)
                    continue
                } catch (_: NotGeneratedException) {
                }
            }

            val value = inputField(meta, depth)
            values[meta.name] = value
        }

        return FieldTools.create(clazz, values)
    }

    private fun inputField(
        meta: FieldMeta,
        depth: Int,
    ): Any? {

        val type = meta.type.classifier as KClass<*>
        if (type.java.isEnum) {
            return inputEnum(meta, type, depth)
        }

        if (!FieldTools.isPrimitive(type)) {
            printPrompt(meta, depth)
            ln(depth)
            return inputObject(type, depth + 1)
        }

        while (true) {

            printPrompt(meta, depth)
            val raw = app.input().readLine()
            if (raw.isBlank()) {
                if (meta.nullable) return null
                err("Поле не может быть null", depth)
                continue
            }

            val converted = FieldTools.primitiveFromString(raw, type)
            if (converted == null) {
                err("Некорректный ввод", depth)
                continue
            }

            val error = meta.validators
                .firstNotNullOfOrNull { it.validate(raw, app) }

            if (error != null) {
                err(error, depth)
                continue
            }

            return converted
        }
    }

    private fun inputEnum(
        meta: FieldMeta,
        enumClass: KClass<*>,
        depth: Int,
    ): Any? {

        val constants = enumClass.java.enumConstants

        printPrompt(meta, depth)
        ln(depth)
        ask("Возможные значения:", depth)
        ln(depth)
        ask(constants.joinToString(" ") { v -> v.toString() }, depth)
        ln(depth)
        while (true) {
            val raw = app.input().readLine()
            if (raw.isBlank() && meta.nullable) return null
            val match = constants.firstOrNull { it.toString() == raw }
            if (match != null) return match
            err("Некорректное значение enum. Повторите ввод.", depth)
        }
    }

    private fun printPrompt(meta: FieldMeta, depth: Int) {
        val label = (meta.prompt ?: "Введите ${meta.name}") + "${if (!meta.nullable) " [!]" else ""}:"
        ask("$label ", depth)
    }

    private fun generateValue(
        meta: FieldMeta,
    ): Any? {
        return meta.generator?.primaryConstructor?.call()?.generate(app)
    }

    private fun ask(msg: String, depth: Int) {
        if (!app.isReadingFromFile()) {
            print(tab.repeat(depth) + msg)
        }
    }

    private fun err(msg: String, depth: Int) {
        if (!app.isReadingFromFile()) {
            println(tab.repeat(depth) + "Ошибка: " + msg)
            return
        }
        val file = app.fileStack.peek()
        val lineNumber = app.input().lineNumber()
        throw ScriptInputException(
            file,
            lineNumber,
            msg
        )
    }

    private fun ln(depth: Int) {
        if (!app.isReadingFromFile()) {
            println(tab.repeat(depth))
            return
        }
    }
}