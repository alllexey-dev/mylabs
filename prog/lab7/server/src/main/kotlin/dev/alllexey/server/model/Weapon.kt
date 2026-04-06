package dev.alllexey.server.model

import kotlinx.serialization.Serializable

@Serializable
enum class Weapon {
    BOLTGUN,
    MELTAGUN,
    FLAMER,
    HEAVY_FLAMER
}