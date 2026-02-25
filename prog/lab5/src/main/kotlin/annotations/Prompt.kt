package dev.alllexey.annotations

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Prompt(
    val value: String,
)
