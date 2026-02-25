package dev.alllexey.model

import dev.alllexey.App
import java.time.LocalDateTime
import kotlin.reflect.KClass

class HashMapCollectionWrapper<T : IdHolder>(
    app: App,
    private val type: KClass<T>,
    private val createdAt: LocalDateTime = LocalDateTime.now()
) : CollectionWrapper<T>(app) {

    private val map: HashMap<Long, T> = HashMap()

    override fun items(): Collection<T> {
        return map.values
    }

    override fun addItem(obj: T) {
        map.put(obj.id(), obj)
    }

    override fun getItemById(id: Long): T? {
        return map[id]
    }

    override fun containsItemById(id: Long): Boolean {
        return map.contains(id)
    }

    override fun removeItem(id: Long) {
        map.remove(id)
    }

    override fun clear() {
        map.clear()
    }

    override fun type(): String = "HashMap<Long, ${type.simpleName}>"

    override fun initializationDate(): LocalDateTime = createdAt

    override fun size(): Int = map.size
}