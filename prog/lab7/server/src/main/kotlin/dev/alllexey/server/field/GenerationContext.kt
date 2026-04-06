package dev.alllexey.server.field

import dev.alllexey.server.user.User

data class GenerationContext(
    val prefilledValues: Map<String, Any>,
    val user: User?,
)
