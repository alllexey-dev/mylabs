package dev.alllexey.server.field

import kotlin.reflect.KType

data class ReflectObjectMeta(
    val type: KType,
    val fields: List<ReflectFieldMeta>,
)