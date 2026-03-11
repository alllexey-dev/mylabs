package dev.alllexey.server.field

import dev.alllexey.server.exception.NotGeneratedException
import java.time.LocalDateTime

interface Generator<T> {
    @Throws(NotGeneratedException::class)
    fun generate(context: GenerationContext): T

    class CurrentDateTimeGenerator : Generator<LocalDateTime> {
        override fun generate(context: GenerationContext): LocalDateTime = LocalDateTime.now()
    }

    class NextIdGenerator : Generator<Long> {
        override fun generate(context: GenerationContext): Long =
            (context.app.collectionWrapper.items().maxOfOrNull { it.id } ?: 0) + 1
    }

    class ProvidedIdGenerator : Generator<Long> {
        override fun generate(context: GenerationContext): Long = context.prefilledValues["id"] as Long? ?: throw NotGeneratedException()
    }

    class ProvidedNameGenerator : Generator<String> {
        override fun generate(context: GenerationContext): String = context.prefilledValues["name"] as String? ?: throw NotGeneratedException()
    }
}
