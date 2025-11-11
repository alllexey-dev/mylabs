package element

import sun.reflect.ReflectionFactory
import annotations.Deserialized
import annotations.Serialized
import java.lang.reflect.Field
import java.lang.reflect.Type

object ElementUtils {

    fun <T> toElement(obj: T): Element? {
        return when (obj) {
            is String -> Element.StringElement(obj)
            is Boolean -> Element.BoolElement(obj)
            is Int -> Element.IntElement(obj)
            is Long -> Element.LongElement(obj)
            is Float -> Element.FloatElement(obj)
            is Double -> Element.DoubleElement(obj)
            is List<*> -> listToElement(obj)
            else -> objToElement(obj)
        }
    }

    fun <T> toObject(element: Element, clazz: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return toObject(element, clazz as Type) as T
    }

    private fun toObject(element: Element, type: Type): Any? {
        return when (type) {
            is java.lang.reflect.ParameterizedType -> {
                val rawType = type.rawType as Class<*>
                if (List::class.java.isAssignableFrom(rawType)) {
                    val elementType = type.actualTypeArguments[0]
                    listElementToList(element, elementType)
                } else {
                    throw IllegalArgumentException("Unsupported generic type: $type")
                }
            }

            is Class<*> -> {
                when (type) {
                    String::class.java -> element.asString()
                    Boolean::class.java -> element.asBool()
                    Int::class.java, Integer::class.java -> element.asInt()
                    Float::class.java, java.lang.Float::class.java -> element.asFloat()
                    Double::class.java, java.lang.Double::class.java -> element.asDouble()
                    Long::class.java, java.lang.Long::class.java -> element.asLong()
                    List::class.java -> throw IllegalArgumentException(
                        "Raw List types are not supported."
                    )

                    else -> objectElementToObject(element, type)
                }
            }

            else -> throw IllegalArgumentException("Unsupported type: $type")
        }
    }

    private fun <T> listToElement(list: List<T>): Element {
        return list.mapNotNull { toElement(it) }.let { Element.ListElement(it) }
    }

    private fun <T> objToElement(obj: T): Element? {
        if (obj == null) return null
        val map = obj.javaClass.declaredFields.mapNotNull {
            val name = getSerializedFieldName(it)
            if (name == null) return@mapNotNull null
            name to (toElement(it.get(obj)) ?: return@mapNotNull null)
        }.associate { it }
        return Element.ObjectElement(map)
    }

    private fun <T> objectElementToObject(element: Element, clazz: Class<T>): T {
        if (element !is Element.ObjectElement) throw IllegalArgumentException("Object element must be Element.ObjectElement")
        val namesToFields = clazz.declaredFields.mapNotNull {
            val name = getDeserializedFieldName(it)
            if (name == null) return@mapNotNull null
            name to it
        }.associate { it }

        val rf = ReflectionFactory.getReflectionFactory()
        val parent = Object::class.java
        val constr = parent.getDeclaredConstructor()
        val reflectConstr = rf.newConstructorForSerialization(clazz, constr)
        val obj = reflectConstr.newInstance()
        clazz.cast(obj)

        val elements = element.elements
        elements.forEach {
            val field = namesToFields[it.key]
            if (field == null) return@forEach
            field.trySetAccessible()
            field.set(obj, toObject(it.value, field.genericType))
        }
        @Suppress("UNCHECKED_CAST")
        return obj as T
    }

    private fun listElementToList(element: Element, elementType: Type): List<*> {
        if (element !is Element.ListElement) {
            return listOf(toObject(element, elementType))
        }

        return element.value.map { itemElement ->
            toObject(itemElement, elementType)
        }
    }

    // do not serialize if null is returned
    private fun getSerializedFieldName(field: Field): String? {
        var name: String? = field.name
        field.trySetAccessible()
        field.getDeclaredAnnotation(Serialized::class.java)?.let {
            println(it.name)
            val annotatedName = it.name
            if (annotatedName != "") name = annotatedName
            if (it.exclude) name = null
        }
        return name
    }

    // do not deserialize if null is returned
    private fun getDeserializedFieldName(field: Field): String? {
        var name: String? = field.name
        field.trySetAccessible()
        field.getDeclaredAnnotation(Deserialized::class.java)?.let {
            val annotatedName = it.name
            if (annotatedName != "") name = annotatedName
            if (it.exclude) name = null
        }
        return name
    }
}