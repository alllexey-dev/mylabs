package dev.alllexey.server.field

import dev.alllexey.common.model.field.Element
import dev.alllexey.common.model.field.FieldType
import java.time.LocalDateTime
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

object ObjectElementBuilder {

    fun fromObject(obj: Any): Element.ObjectElement {

        val clazz = obj::class
        val meta = ObjectMetaMapper.buildMeta(clazz)

        val properties = clazz.memberProperties
            .associateBy { it.name }

        val map = mutableMapOf<String, Element>()

        for (field in meta.fields) {
            val prop = properties[field.fieldName] as KProperty1<Any, *>
            val value = prop.get(obj)
            map[field.fieldName] = convertValue(value)
        }

        return Element.ObjectElement(
            value = map,
            type = FieldType.ObjectType(meta)
        )
    }

    private fun convertValue(value: Any?): Element {
        if (value == null) return Element.NullElement

        return when (value) {

            is String -> Element.StringElement(value)
            is Boolean -> Element.BooleanElement(value)
            is Int -> Element.IntegerElement(value)
            is Long -> Element.LongElement(value)
            is Float -> Element.FloatElement(value)
            is Double -> Element.DoubleElement(value)

            is LocalDateTime ->
                Element.StringElement(value.toString())

            is Enum<*> ->
                Element.EnumElement(
                    value.name,
                    FieldType.EnumType(value.javaClass.enumConstants.map { it.toString() })
                )

            else -> {
                val nested = fromObject(value)
                nested
            }
        }
    }

}