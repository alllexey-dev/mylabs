package dev.alllexey.server.data

import dev.alllexey.server.App
import dev.alllexey.server.model.IdHolder
import dev.alllexey.server.user.User

abstract class CollectionManager<T : IdHolder>(
    val app: App
) {

    abstract fun items(): Collection<T>

    abstract fun addItem(obj: T)

    abstract fun getItemById(id: Long): T?

    abstract fun containsItemById(id: Long): Boolean

    abstract fun removeItem(id: Long, user: User?)

    abstract fun clear(user: User?)

    abstract fun type(): String
    abstract fun size(): Int
    abstract fun modifyItem(obj: T, user: User?)
}