package dev.alllexey.server.field

import dev.alllexey.common.model.field.*
import dev.alllexey.server.exception.NotGeneratedException
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

object ObjectGenerator {

    fun <T : Any> generate(element: Element.ObjectElement, context: GenerationContext, clazz: KClass<T>): T {
        val meta = ReflectObjectMetaMapper.build(clazz)
        return generate(element, meta, context) as T
    }

    fun generate(
        element: Element.ObjectElement,
        meta: ReflectObjectMeta,
        context: GenerationContext,
    ): Any {

        val clazz = meta.type.classifier as KClass<*>
        val ctor = clazz.primaryConstructor
            ?: error("No constructor")

        val values = mutableMapOf<String, Any?>()

        fieldsLoop@ for (field in meta.fields) {
            val provided = element.value[field.fieldName]

            if (provided != null) {
                val value = convertElement(provided, field.type, context)
                values[field.fieldName] = value
                continue
            }

            generatorLoop@ for (generator in field.generators) {
                    try {
                        val genInstance = generator.primaryConstructor!!.call()
                        val generated = genInstance.generate(context) ?: continue@generatorLoop

                        values[field.fieldName] = generated

                        continue@fieldsLoop
                    } catch (_: NotGeneratedException) {
                    }
                }

            if (!field.nullable) error("Missing required field ${field.fieldName}")

            values[field.fieldName] = null
        }

        return ctor.callBy(
            ctor.parameters.associateWith { values[it.name] }
        )
    }

    private fun convertElement(
        element: Element,
        type: KType,
        context: GenerationContext,
    ): Any? {

        val clazz = type.classifier as KClass<*>
        return when {

            element is Element.StringElement -> element.value
            element is Element.BooleanElement -> element.value
            element is Element.IntegerElement -> element.value
            element is Element.LongElement -> element.value
            element is Element.FloatElement -> element.value
            element is Element.DoubleElement -> element.value
            element is Element.LocalDateTimeElement -> element.value
            element is Element.NullElement -> null

            element is Element.EnumElement -> {
                val enumClass = type.classifier as KClass<*>
                enumClass.java.enumConstants
                    .first { it.toString() == element.value }
            }

            element is Element.ObjectElement -> {
                val nestedMeta =
                    ReflectObjectMetaMapper.build(type.classifier as KClass<*>)

                generate(element, nestedMeta, context)
            }

            else -> error("Unsupported element: $element")
        }
    }
}