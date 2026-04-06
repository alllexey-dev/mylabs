package dev.alllexey.common.model.field

import kotlinx.serialization.Serializable

@Serializable
class FieldMeta(
    val fieldName: String,
    val localizedName: String,
    val type: FieldType,
    val nullable: Boolean,
    val validators: List<Validator>,
    val skip: Boolean,
)

