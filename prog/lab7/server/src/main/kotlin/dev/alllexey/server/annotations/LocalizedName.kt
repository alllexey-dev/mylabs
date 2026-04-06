package dev.alllexey.server.annotations

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class LocalizedName(
    val value: String,
)
