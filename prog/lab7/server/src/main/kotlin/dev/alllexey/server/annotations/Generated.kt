package dev.alllexey.server.annotations

import dev.alllexey.server.field.Generator
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Generated (
    // first one to generate value will be used
    vararg val generators: KClass<out Generator<*>>
)