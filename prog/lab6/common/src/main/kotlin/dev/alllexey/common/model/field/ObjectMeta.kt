package dev.alllexey.common.model.field

import kotlinx.serialization.Serializable

@Serializable
data class ObjectMeta(
    val className: String,
    val fields: List<FieldMeta>,
)