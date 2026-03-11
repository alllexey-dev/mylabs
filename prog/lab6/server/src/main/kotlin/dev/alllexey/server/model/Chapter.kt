package dev.alllexey.server.model

import dev.alllexey.server.annotations.LocalizedName
import dev.alllexey.server.annotations.Validate
import kotlinx.serialization.Serializable

@Serializable
data class Chapter(

    @Validate("not_blank")
    @LocalizedName("имя части")
    val name: String,

    @LocalizedName("родительский легион")
    val parentLegion: String?,

    @Validate(">0", "<=1000")
    @LocalizedName("количество кораблей")
    val marinesCount: Long,
    @LocalizedName("название мира")
    val world: String?,
)