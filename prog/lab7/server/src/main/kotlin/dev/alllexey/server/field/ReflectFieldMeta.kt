package dev.alllexey.server.field

import dev.alllexey.common.model.field.Validator
import kotlin.reflect.KClass
import kotlin.reflect.KType

data class ReflectFieldMeta(
    val fieldName: String,
    val localizedName: String,
    val type: KType,
    val nullable: Boolean,
    val validators: List<Validator>,
    val generators: List<KClass<out Generator<*>>>,
)