package dev.alllexey.common.io

import dev.alllexey.common.model.field.Element
import dev.alllexey.common.model.field.FieldMeta
import dev.alllexey.common.model.field.FieldType
import dev.alllexey.common.model.field.FieldType.Companion.verify
import dev.alllexey.common.model.field.ObjectMeta
import java.io.PrintStream
import java.time.LocalDateTime

class ElementInputHelper(
    val inputProvider: () -> InputProvider,
    val outputProvider: () -> PrintStream?, // if null and wrong input then throw
) {

    private val tab = "  "

    fun inputElement(
        objectMeta: ObjectMeta,
        prefilled: Map<String, String> = emptyMap(),
        path: String = "",
        depth: Int = 0,
    ): Element.ObjectElement {

        val values = mutableMapOf<String, Element>()

        for (field in objectMeta.fields) {
            if (field.skip) continue
            val element = inputField(field, prefilled, path, depth)
            if (element != null) {
                values[field.fieldName] = element
            }
        }

        return Element.ObjectElement(
            value = values,
            type = FieldType.ObjectType(objectMeta)
        )
    }

    private fun inputField(
        meta: FieldMeta,
        prefilled: Map<String, String>,
        path: String,
        depth: Int,
    ): Element? {

        val type = meta.type

        if (type is FieldType.ObjectType) {
            printPrompt(meta, depth)
            output()?.println()
            return inputElement(type.objectMeta, prefilled, path + meta.fieldName + ".", depth + 1)
        }

        if (type is FieldType.EnumType) {
            return inputEnum(meta, type, depth)
        }

        val prefilledValue = prefilled[path + meta.fieldName]
        if (prefilledValue != null) {
            if (!type.verify(prefilledValue)) {
                throw RuntimeException("Введённое ранее значение для поля ${path + meta.fieldName} некорректно")
            }

            val validationError = meta.validators
                .firstNotNullOfOrNull { it.validate(prefilledValue) }

            if (validationError != null) {
                throw RuntimeException("Введённое ранее значение для поля ${path + meta.fieldName} некорректно: $validationError")
            }

            return convertToElement(type, prefilledValue)
        }

        while (true) {

            printPrompt(meta, depth)
            val raw = input().readLine()

            if (raw.isBlank()) {
                if (meta.nullable) return null
                err("Поле не может быть null", depth)
                continue
            }

            if (!type.verify(raw)) {
                err("Некорректный ввод", depth)
                continue
            }

            val validationError = meta.validators
                .firstNotNullOfOrNull { it.validate(raw) }

            if (validationError != null) {
                err(validationError, depth)
                continue
            }

            return convertToElement(type, raw)
        }
    }

    private fun inputEnum(
        meta: FieldMeta,
        enumType: FieldType.EnumType,
        depth: Int,
    ): Element.EnumElement? {

        printPrompt(meta, depth)
        output()?.println()
        ask("Возможные значения:", depth)
        output()?.println()
        ask(enumType.entries.joinToString(" "), depth)
        output()?.println()

        while (true) {

            val raw = input().readLine()

            if (raw.isBlank() && meta.nullable) return null

            if (enumType.entries.contains(raw)) {
                return Element.EnumElement(raw, enumType)
            }

            err("Некорректное значение enum. Повторите ввод.", depth)
        }
    }

    private fun convertToElement(type: FieldType, raw: String): Element {
        return when (type) {
            FieldType.StringType -> Element.StringElement(raw)
            FieldType.BooleanType -> Element.BooleanElement(raw.toBoolean())
            FieldType.IntegerType -> Element.IntegerElement(raw.toInt())
            FieldType.LongType -> Element.LongElement(raw.toLong())
            FieldType.FloatType -> Element.FloatElement(raw.toFloat())
            FieldType.DoubleType -> Element.DoubleElement(raw.toDouble())
            FieldType.LocalDateTimeType -> Element.LocalDateTimeElement(LocalDateTime.parse(raw))

            else -> error("Unsupported type")
        }
    }

    private fun printPrompt(meta: FieldMeta, depth: Int) {
        val label = "Введите ${meta.localizedName}${if (!meta.nullable) " [!]" else ""}: "
        ask(label, depth)
    }

    private fun ask(msg: String, depth: Int) {
        if (output() != null) {
            print(tab.repeat(depth) + msg)
        }
    }

    private fun err(msg: String, depth: Int) {
        if (output() != null) {
            println(tab.repeat(depth) + "Ошибка: $msg")
            return
        }

        throw RuntimeException(msg)
    }

    private fun input(): InputProvider = inputProvider.invoke()

    private fun output(): PrintStream? = outputProvider.invoke()
}