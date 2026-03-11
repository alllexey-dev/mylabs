package dev.alllexey.server.field

import dev.alllexey.common.model.field.FieldMeta
import dev.alllexey.common.model.field.FieldType
import dev.alllexey.common.model.field.ObjectMeta
import dev.alllexey.server.annotations.Generated
import dev.alllexey.server.annotations.Ignored
import dev.alllexey.server.annotations.LocalizedName
import dev.alllexey.server.annotations.Validate
import java.time.LocalDateTime
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

object ObjectMetaMapper {

    private val cache = mutableMapOf<KClass<*>, ObjectMeta>()

    fun buildMeta(clazz: KClass<*>): ObjectMeta {
        cache[clazz]?.let { return it }

        val constructor = clazz.primaryConstructor
            ?: error("Class ${clazz.simpleName} must have primary constructor")

        val fields = constructor.parameters.mapNotNull { param ->
            buildFieldMeta(param)
        }

        val meta = ObjectMeta(
            className = clazz.simpleName ?: throw NullPointerException("Class $clazz must have a name"),
            fields = fields
        )

        cache[clazz] = meta
        return meta
    }

    // if null, skip this field
    private fun buildFieldMeta(param: KParameter): FieldMeta? {
        val name = param.name ?: error("Unnamed parameter")

        val localizedName = param.findAnnotation<LocalizedName>()?.value ?: name

        val validators = param.findAnnotation<Validate>()?.rules?.map {
                ValidatorParser.parseValidator(it)
            } ?: emptyList()

        val ignored = param.findAnnotation<Ignored>() != null
        val type = resolveFieldType(param.type)

        return FieldMeta(
            fieldName = name,
            localizedName = localizedName,
            type = type,
            nullable = param.type.isMarkedNullable,
            validators = validators,
            skip = ignored
        )
    }

    private fun resolveFieldType(type: KType): FieldType {
        val clazz = type.jvmErasure

        return when (clazz) {
            String::class -> FieldType.StringType
            Boolean::class -> FieldType.BooleanType
            Int::class -> FieldType.IntegerType
            Long::class -> FieldType.LongType
            Float::class -> FieldType.FloatType
            Double::class -> FieldType.DoubleType
            LocalDateTime::class -> FieldType.LocalDateTimeType

            else -> {
                when {
                    clazz.java.isEnum -> {
                        FieldType.EnumType(
                            clazz.java.enumConstants.map { it.toString() }
                        )
                    }

                    else -> {
                        FieldType.ObjectType(buildMeta(clazz))
                    }
                }
            }
        }
    }


}