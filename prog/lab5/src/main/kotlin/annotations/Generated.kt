package dev.alllexey.annotations

import dev.alllexey.App
import dev.alllexey.exceptions.NotGeneratedException
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Generated(
    val generator: KClass<out Generator<*>>
)

interface Generator<T> {
    @Throws(NotGeneratedException::class)
    fun generate(app: App): T
}
