package dev.alllexey.common.model.field

import kotlinx.serialization.Serializable

@Serializable
sealed class FieldType {
    @Serializable object StringType : FieldType()
    @Serializable object BooleanType : FieldType()
    @Serializable object IntegerType : FieldType()
    @Serializable object LongType : FieldType()
    @Serializable object FloatType : FieldType()
    @Serializable object DoubleType : FieldType()
    @Serializable object LocalDateTimeType : FieldType()
    @Serializable data class ObjectType(val objectMeta: ObjectMeta) : FieldType()
    @Serializable data class EnumType(val entries: List<String>) : FieldType()


    companion object {
        fun FieldType.verify(str: String): Boolean {
            val res = when (this) {
                StringType -> str
                BooleanType -> str.toBooleanStrictOrNull()
                DoubleType -> str.toDoubleOrNull()
                FloatType -> str.toFloatOrNull()
                IntegerType -> str.toIntOrNull()
                LongType -> str.toLongOrNull()
                LocalDateTimeType -> null
                is ObjectType -> null
                is EnumType -> this.entries.contains(str)
            }
            return res != null
        }
    }
}