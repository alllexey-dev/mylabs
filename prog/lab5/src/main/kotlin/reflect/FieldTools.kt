package dev.alllexey.reflect

import dev.alllexey.annotations.Generated
import dev.alllexey.annotations.Prompt
import dev.alllexey.annotations.Validate
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

object FieldTools {

    fun fieldMetas(clazz: KClass<*>): List<FieldMeta> {
        val constructor = clazz.primaryConstructor
            ?: error("Класс должен иметь primary constructor")

        return constructor.parameters.map { param ->

            val property = clazz.memberProperties
                .first { it.name == param.name }

            FieldMeta(
                name = property.name,
                type = param.type,
                prompt = param.findAnnotation<Prompt>()?.value,
                nullable = param.type.isMarkedNullable,
                validators = param.findAnnotation<Validate>()
                    ?.validators
                    ?.mapNotNull { it.primaryConstructor?.call() }
                    ?: emptyList(),
                generator = param.findAnnotation<Generated>()?.generator
            )
        }
    }

    fun <T : Any> create(
        clazz: KClass<T>,
        values: Map<String, Any?>
    ): T {
        val constructor = clazz.primaryConstructor
            ?: error("Класс должен иметь primary constructor")

        val args = mutableMapOf<KParameter, Any?>()

        for (param in constructor.parameters) {
            val name = param.name!!

            if (values.containsKey(name)) {
                args[param] = values[name]
            } else if (!param.isOptional && !param.type.isMarkedNullable) {
                error("Missing required field: $name")
            }
        }

        return constructor.callBy(args)
    }

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
