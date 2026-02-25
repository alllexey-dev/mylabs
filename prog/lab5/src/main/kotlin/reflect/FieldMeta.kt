package dev.alllexey.reflect

import dev.alllexey.annotations.Generator
import dev.alllexey.annotations.Validator
import kotlin.reflect.KClass
import kotlin.reflect.KType

data class FieldMeta(
    val name: String,
    val type: KType,
    val prompt: String?,
    val nullable: Boolean,
    val validators: List<Validator>,
    val generator: KClass<out Generator<*>>?,
)