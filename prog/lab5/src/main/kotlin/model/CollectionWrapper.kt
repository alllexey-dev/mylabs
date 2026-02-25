package dev.alllexey.model

import dev.alllexey.App
import java.time.LocalDateTime

abstract class CollectionWrapper<T : IdHolder>(
    val app: App
) {

    abstract fun items(): Collection<T>

    abstract fun addItem(obj: T)

    abstract fun getItemById(id: Long): T?

    abstract fun containsItemById(id: Long): Boolean

    abstract fun removeItem(id: Long)

    abstract fun clear()

    abstract fun type(): String
    abstract fun initializationDate(): LocalDateTime
    abstract fun size(): Int
}