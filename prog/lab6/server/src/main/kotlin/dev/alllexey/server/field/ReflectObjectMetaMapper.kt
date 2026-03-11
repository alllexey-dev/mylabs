package dev.alllexey.server.field

import dev.alllexey.server.annotations.Generated
import dev.alllexey.server.annotations.LocalizedName
import dev.alllexey.server.annotations.Validate
import kotlin.collections.emptyList
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters

object ReflectObjectMetaMapper {

    private val cache = mutableMapOf<KClass<*>, ReflectObjectMeta>()

    fun build(clazz: KClass<*>): ReflectObjectMeta {
        cache[clazz]?.let { return it }

        val constructor = clazz.primaryConstructor
            ?: error("Class ${clazz.simpleName} must have primary constructor")

        val fields = constructor.valueParameters.map { param ->

            val localized = param.annotations
                .filterIsInstance<LocalizedName>()
                .firstOrNull()?.value ?: param.name!!

            val validators = param.findAnnotation<Validate>()?.rules?.map {
                ValidatorParser.parseValidator(it)
            } ?: emptyList()

            val generators =
                param.findAnnotation<Generated>()
                    ?.generators?.map { it } ?: emptyList()

            ReflectFieldMeta(
                fieldName = param.name!!,
                localizedName = localized,
                type = param.type,
                nullable = param.type.isMarkedNullable,
                validators = validators,
                generators = generators
            )
        }

        val meta = ReflectObjectMeta(
            type = clazz.createType(),
            fields = fields
        )

        cache[clazz] = meta
        return meta
    }
}