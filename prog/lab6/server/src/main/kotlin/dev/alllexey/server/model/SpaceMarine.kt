package dev.alllexey.server.model

import dev.alllexey.common.serialization.LocalDateTimeSerializer
import dev.alllexey.server.annotations.Generated
import dev.alllexey.server.annotations.Ignored
import dev.alllexey.server.annotations.LocalizedName
import dev.alllexey.server.annotations.Validate
import dev.alllexey.server.field.Generator
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class SpaceMarine(

    @Ignored
    @Generated(Generator.ProvidedIdGenerator::class, Generator.NextIdGenerator::class)
    override val id: Long,

    @Generated(Generator.ProvidedNameGenerator::class)
    @Validate("not_blank")
    @LocalizedName("имя корабля")
    val name: String,

    @LocalizedName("координаты")
    val coordinates: Coordinates,

    @Ignored
    @Generated(Generator.CurrentDateTimeGenerator::class)
    @Serializable(LocalDateTimeSerializer::class)
    val creationDate: LocalDateTime,

    @Validate(">0")
    @LocalizedName("уровень здоровья")
    val health: Double,

    @LocalizedName("категория")
    val category: AstartesCategory,
    @LocalizedName("тип оружия")
    val weaponType: Weapon?,
    @LocalizedName("тип оружия ближнего боя")
    val meleeWeapon: MeleeWeapon?,
    @LocalizedName("данные части")
    val chapter: Chapter,
) : IdHolder

