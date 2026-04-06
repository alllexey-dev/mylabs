package dev.alllexey.server.data

import dev.alllexey.common.model.field.Element
import dev.alllexey.common.model.field.FieldType
import dev.alllexey.common.model.field.ObjectMeta
import dev.alllexey.server.annotations.Generated
import dev.alllexey.server.data.DatabaseCollectionProxy.Companion.SEPARATOR
import dev.alllexey.server.field.GenerationContext
import dev.alllexey.server.field.ObjectGenerator
import dev.alllexey.server.field.ObjectMetaMapper
import kotlin.reflect.KClass
import java.sql.ResultSet
import java.sql.Timestamp

object ResultSetMapper {

    fun <T : Any> mapRow(
        rs: ResultSet,
        type: KClass<T>,
    ): T {
        val flat = resultSetToFlatMap(rs)

        val meta = ObjectMetaMapper.buildMeta(type)
        val element = flatMapToElement(flat, meta)

        val context = GenerationContext(mapOf(), null)

        return ObjectGenerator.generate(element, context, type)
    }

    private fun resultSetToFlatMap(rs: ResultSet): Map<String, Any?> {
        val meta = rs.metaData
        val map = mutableMapOf<String, Any?>()

        for (i in 1..meta.columnCount) {
            val column = meta.getColumnLabel(i)
            val value = rs.getObject(i)
            map[column] = value
        }

        return map
    }

    private fun flatMapToElement(
        flat: Map<String, Any?>,
        meta: ObjectMeta
    ): Element.ObjectElement {
        return Element.ObjectElement(
            value = build(flat, null, meta),
            type = FieldType.ObjectType(meta)
        )
    }

    private fun build(flat: Map<String, Any?>, prefix: String?, meta: ObjectMeta): Map<String, Element> {
        val result = mutableMapOf<String, Element>()

        for (field in meta.fields) {

            val fullName = if (prefix == null)
                field.fieldName
            else prefix + SEPARATOR + field.fieldName

            when (val type = field.type) {
                is FieldType.ObjectType -> {
                    val nested = build(flat, fullName.lowercase(), type.objectMeta)

                    result[field.fieldName] = Element.ObjectElement(
                        value = nested,
                        type = type
                    )
                }

                is FieldType.EnumType -> {
                    val value = flat[fullName.lowercase()]

                    result[field.fieldName] =
                        if (value == null) Element.NullElement
                        else Element.EnumElement(
                            value.toString(),
                            type
                        )
                }

                else -> {
                    val value = flat[fullName.lowercase()]
                    result[field.fieldName] = toElement(value)
                }
            }
        }

        return result
    }


    private fun toElement(value: Any?): Element {
        return when (value) {
            null -> Element.NullElement

            is String -> Element.StringElement(value)
            is Boolean -> Element.BooleanElement(value)
            is Int -> Element.IntegerElement(value)
            is Long -> Element.LongElement(value)
            is Float -> Element.FloatElement(value)
            is Double -> Element.DoubleElement(value)

            is Timestamp ->
                Element.LocalDateTimeElement(value.toLocalDateTime())

            is java.sql.Date ->
                Element.StringElement(value.toString())

            else -> error("Unsupported DB value: $value (${value::class})")
        }
    }
}