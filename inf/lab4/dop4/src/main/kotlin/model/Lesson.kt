package model

data class Lesson(
    val times: String? = null,
    val weeks: List<Int>? = null,
    val group: String? = null,
    val room: String? = null,
    val building: String? = null,
    val subject: String? = null,
    val format: String? = null,
)