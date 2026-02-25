package dev.alllexey.model

import dev.alllexey.annotations.Generated
import dev.alllexey.annotations.Prompt
import dev.alllexey.annotations.Validate
import java.time.LocalDateTime

data class SpaceMarine(
    @Generated(NextIdGenerator::class)
    val id: Long,

    @Generated(ProvidedNameGenerator::class)
    @Prompt("Введите название")
    @Validate(NotEmptyValidator::class)
    val name: String,

    @Prompt("Введите координаты")
    val coordinates: Coordinates,

    @Generated(CurrentDateTimeGenerator::class)
    val creationDate: LocalDateTime,

    @Validate(GreaterThanZeroValidator::class)
    @Prompt("Введите уровень здоровья (>0)")
    val health: Double,

    @Prompt("Введите категорию")
    val category: AstartesCategory,
    @Prompt("Введите тип оружия")
    val weaponType: Weapon?,
    @Prompt("Введите тип оружия ближнего боя")
    val meleeWeapon: MeleeWeapon?,
    @Prompt("Введите данные части")
    val chapter: Chapter,
) : IdHolder, Comparable<SpaceMarine> {

    override fun id(): Long = id

    override fun compareTo(other: SpaceMarine): Int {
        return name.lowercase().compareTo(other.name.lowercase())
    }
}

data class Coordinates(
    @Prompt("Введите координату X")
    val x: Float,

    @Validate(Max759Validator::class)
    @Prompt("Введите координату Y (<=759)")
    val y: Int,
)

data class Chapter(

    @Validate(NotEmptyValidator::class)
    @Prompt("Введите имя части")
    val name: String,

    @Prompt("Введите родительский легион")
    val parentLegion: String?,

    @Validate(GreaterThanZeroValidator::class, Max1000Validator::class)
    @Prompt("Введите количество кораблей (>0, <=1000)")
    val marinesCount: Long,
    @Prompt("Введите название мира")
    val world: String?,


) : Comparable<Chapter> {

    override fun compareTo(other: Chapter): Int {
        return name.lowercase().compareTo(other.name.lowercase())
    }
}

enum class AstartesCategory() {
    ASSAULT,
    TACTICAL,
    HELIX
}

enum class Weapon {
    BOLTGUN,
    MELTAGUN,
    FLAMER,
    HEAVY_FLAMER
}

enum class MeleeWeapon {
    CHAIN_SWORD,
    POWER_SWORD,
    CHAIN_AXE,
    MANREAPER,
    POWER_FIST
}
