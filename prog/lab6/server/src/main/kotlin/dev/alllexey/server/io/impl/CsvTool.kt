package dev.alllexey.io.impl

import dev.alllexey.server.io.Serializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.decodeFromString
import kotlin.reflect.KClass

@OptIn(ExperimentalSerializationApi::class)
class CsvTool<T : Any>(
    private val serializer: KSerializer<T>,
    private val listSerializer: KSerializer<List<T>> = ListSerializer(serializer)
) : Serializer<T> {

    val csv = Csv { hasHeaderRecord = true }

    override fun serializeCollection(list: Collection<T>): String {
        return csv.encodeToString(listSerializer, list.toList())
    }

    override fun serialize(obj: T): String {
        return csv.encodeToString(serializer, obj)
    }

    override fun deserializeCollection(string: String, type: KClass<T>): Collection<T> {
        return csv.decodeFromString(listSerializer, string)
    }

    override fun deserialize(str: String, type: KClass<T>): T {
        return csv.decodeFromString(serializer, str)
    }
}