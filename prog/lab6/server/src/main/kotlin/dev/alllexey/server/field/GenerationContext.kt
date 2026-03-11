package dev.alllexey.server.field

import dev.alllexey.server.App

data class GenerationContext(
    val app: App,
    val prefilledValues: Map<String, Any>,
)
