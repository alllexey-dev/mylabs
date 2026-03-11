package dev.alllexey.common.model.field

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.KClass

object FieldTools {

    fun isPrimitive(type: KClass<*>): Boolean {
        return type in setOf(
            String::class,
            Int::class,
            Long::class,
            Double::class,
            Float::class,
            Boolean::class,
            LocalDate::class,
            LocalDateTime::class,
        )
    }
    
    fun primitiveFromString(
        raw: String,
        clazz: KClass<*>,
    ): Any? {

        return try {
            when (clazz) {
                String::class -> raw
                Int::class -> raw.toInt()
                Long::class -> raw.toLong()
                Float::class -> raw.toFloat()
                Double::class -> raw.toDouble()
                Boolean::class -> raw.toBooleanStrict()
                LocalDate::class -> LocalDate.parse(raw)
                LocalDateTime::class -> LocalDateTime.parse(raw)
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }

    fun primitiveToString(
        t: Any,
    ): String {
        return t.toString()
    }
}
