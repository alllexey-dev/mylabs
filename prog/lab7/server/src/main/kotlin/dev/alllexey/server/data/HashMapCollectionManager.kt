package dev.alllexey.server.data

import dev.alllexey.server.App
import dev.alllexey.server.model.IdHolder
import dev.alllexey.server.user.User
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.reflect.KClass

class HashMapCollectionManager<T : IdHolder>(
    app: App,
    private val type: KClass<T>,
) : CollectionManager<T>(app) {

    private val map: HashMap<Long, T> = HashMap()
    private val lock = ReentrantReadWriteLock()

    override fun items(): Collection<T> = lock.read {
        map.values.toList()
    }

    override fun addItem(obj: T) = lock.write {
        map[obj.id] = obj
    }

    override fun modifyItem(obj: T, user: User?) = lock.write {
        map[obj.id] = obj
    }

    override fun getItemById(id: Long): T? = lock.read {
        map[id]
    }

    override fun containsItemById(id: Long): Boolean = lock.read {
        map.containsKey(id)
    }

    override fun removeItem(id: Long, user: User?) = lock.write {
        map.remove(id)
        return@write
    }

    override fun clear(user: User?) = lock.write {
        map.clear()
    }

    override fun type(): String = "HashMap<Long, ${type.simpleName}>"

    override fun size(): Int = lock.read {
        map.size
    }
}