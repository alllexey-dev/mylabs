package dev.alllexey.io.impl

import dev.alllexey.io.Serializer
import dev.alllexey.reflect.FieldTools
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.text.iterator

class CsvTool<T : Any> : Serializer<T> {

    override fun serializeCollection(list: Collection<T>): String {
        return list.joinToString("\n") { serialize(it) }
    }

    override fun serialize(obj: T): String {
        @Suppress("UNCHECKED_CAST")
        val tokens = flattenObject(obj, obj::class as KClass<Any>)
        return tokens.joinToString(",")
    }

    override fun deserializeCollection(string: String, type: KClass<T>): Collection<T> {
        return string.split("\n").map { deserialize(it, type) }
    }

    override fun deserialize(str: String, type: KClass<T>): T {
        val tokens = parseCsvLine(str)
        val tokenQueue = LinkedList(tokens)
        return buildObject(type, tokenQueue)
    }

    private fun flattenObject(obj: Any?, type: KClass<Any>): List<String> {
        if (obj == null) {
            return generateBlankColumns(type)
        }

        if (FieldTools.isPrimitive(type)) {
            val strVal = FieldTools.primitiveToString(obj)
            return listOf(escape(strVal))
        }

        if (type.java.isEnum) {
            return listOf(escape(obj.toString()))
        }

        val metas = FieldTools.fieldMetas(type)
        val results = mutableListOf<String>()

        for (meta in metas) {
            val prop = type.memberProperties.firstOrNull { it.name == meta.name }
                ?: error("Property ${meta.name} not found in ${type.simpleName}")

            val value = prop.call(obj)

            @Suppress("UNCHECKED_CAST")
            results.addAll(flattenObject(value, meta.type.classifier as KClass<Any>))
        }

        return results
    }

    private fun generateBlankColumns(type: KClass<Any>): List<String> {
        if (FieldTools.isPrimitive(type) || type.java.isEnum) {
            return listOf("")
        }

        val metas = FieldTools.fieldMetas(type)
        return metas.flatMap { meta ->
            @Suppress("UNCHECKED_CAST")
            generateBlankColumns(meta.type.classifier as KClass<Any>)
        }
    }

    private fun escape(data: String): String {
        var escapedData = data.replace("\"", "\"\"")
        if (data.contains(",") || data.contains("\"")) {
            escapedData = "\"$escapedData\""
        }
        return escapedData
    }

    private fun <R : Any> buildObject(type: KClass<R>, tokens: Queue<String>): R {
        if (FieldTools.isPrimitive(type)) {
            val token = tokens.poll() ?: ""
            val value = FieldTools.primitiveFromString(unescape(token), type)
            @Suppress("UNCHECKED_CAST")
            return value as R
        }

        if (type.java.isEnum) {
            val token = tokens.poll() ?: ""
            val cleanToken = unescape(token)
            if (cleanToken.isBlank()) error("Enum value cannot be empty")

            val enumConstants = type.java.enumConstants
            return enumConstants.firstOrNull { it.toString() == cleanToken } as? R
                ?: error("Unknown enum value: $cleanToken for type ${type.simpleName}")
        }

        val metas = FieldTools.fieldMetas(type)
        val values = mutableMapOf<String, Any?>()

        for (meta in metas) {
            val fieldType = meta.type.classifier as KClass<*>

            val value = try {
                buildObject(fieldType, tokens)
            } catch (e: Exception) {
                if (meta.nullable) null else throw e
            }

            values[meta.name] = value
        }

        return FieldTools.create(type, values)
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val builder = StringBuilder()
        var inQuotes = false

        for (char in line) {
            when (char) {
                '"' -> {
                    inQuotes = !inQuotes
                    builder.append(char)
                }
                ',' -> {
                    if (!inQuotes) {
                        result.add(builder.toString())
                        builder.clear()
                    } else {
                        builder.append(char)
                    }
                }
                else -> builder.append(char)
            }
        }
        result.add(builder.toString())

        return result
    }

    private fun unescape(data: String): String {
        var result = data
        if (result.startsWith("\"") && result.endsWith("\"")) {
            result = result.substring(1, result.length - 1)
        }
        return result.replace("\"\"", "\"")
    }
}