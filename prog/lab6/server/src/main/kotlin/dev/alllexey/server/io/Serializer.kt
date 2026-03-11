package dev.alllexey.server.io

import kotlin.reflect.KClass

interface Serializer<T : Any> {

    fun serializeCollection(list: Collection<T>): String

    fun serialize(obj: T): String

    fun deserializeCollection(string: String, type: KClass<T>): Collection<T>

    fun deserialize(str: String, type: KClass<T>): T
}