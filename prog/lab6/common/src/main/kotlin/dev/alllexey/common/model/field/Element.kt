package dev.alllexey.common.model.field

import dev.alllexey.common.serialization.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
sealed class Element {
    abstract val type: FieldType

    @Serializable
    object NullElement : Element() {
        override val type = FieldType.StringType

        override fun toString(): String {
            return "null"
        }
    }

    @Serializable
    data class StringElement(
        val value: String
    ) : Element() {
        override val type = FieldType.StringType

        override fun toString(): String {
            return value
        }
    }

    @Serializable
    data class BooleanElement(
        val value: Boolean
    ) : Element() {
        override val type = FieldType.BooleanType

        override fun toString(): String {
            return value.toString()
        }
    }

    @Serializable
    data class IntegerElement(
        val value: Int
    ) : Element() {
        override val type = FieldType.IntegerType

        override fun toString(): String {
            return value.toString()
        }
    }

    @Serializable
    data class LongElement(
        val value: Long
    ) : Element() {
        override val type = FieldType.LongType

        override fun toString(): String {
            return value.toString()
        }
    }

    @Serializable
    data class FloatElement(
        val value: Float
    ) : Element() {
        override val type = FieldType.FloatType

        override fun toString(): String {
            return value.toString()
        }
    }

    @Serializable
    data class DoubleElement(
        val value: Double
    ) : Element() {
        override val type = FieldType.DoubleType

        override fun toString(): String {
            return value.toString()
        }
    }

    @Serializable
    data class LocalDateTimeElement(
        @Serializable(LocalDateTimeSerializer::class)
        val value: LocalDateTime
    ) : Element() {
        override val type = FieldType.LocalDateTimeType

        override fun toString(): String {
            return value.toString()
        }
    }

    @Serializable
    data class ObjectElement(
        val value: Map<String, Element>,
        override val type: FieldType.ObjectType
    ) : Element() {
        override fun toString(): String { return "(" + value.entries.joinToString(",") { it.key + "=" + it.value.toString() } + ")" }
    }

    @Serializable
    data class EnumElement(
        val value: String,
        override val type: FieldType.EnumType
    ) : Element() {
        override fun toString(): String {
            return value
        }
    }
}