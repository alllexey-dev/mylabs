package dev.alllexey.annotations

import dev.alllexey.App
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Validate(
    vararg val validators: KClass<out Validator>
)

interface Validator {
    fun validate(value: String, app: App): String? // return null to pass
}
