package dev.alllexey.server.model

import dev.alllexey.server.annotations.LocalizedName
import dev.alllexey.server.annotations.Validate
import kotlinx.serialization.Serializable

@Serializable
data class Coordinates(
    @LocalizedName("координата 'x'")
    val x: Float,

    @Validate("<=759")
    @LocalizedName("координата 'y'")
    val y: Int,
)
