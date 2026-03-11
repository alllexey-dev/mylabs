package dev.alllexey.server.annotations

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Validate(
    vararg val rules: String
)
