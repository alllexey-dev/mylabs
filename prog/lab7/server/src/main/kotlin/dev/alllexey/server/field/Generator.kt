package dev.alllexey.server.field

import dev.alllexey.server.exception.NotGeneratedException
import java.time.LocalDateTime

interface Generator<T> {
    @Throws(NotGeneratedException::class)
    fun generate(context: GenerationContext): T

    class DummyIdGenerator : Generator<Long> {
        override fun generate(context: GenerationContext): Long = -1
    }

    class ProvidedDateTimeGenerator : Generator<LocalDateTime> {
        override fun generate(context: GenerationContext): LocalDateTime =
            context.prefilledValues["dateTime"] as? LocalDateTime? ?: throw NotGeneratedException()
    }

    class CurrentDateTimeGenerator : Generator<LocalDateTime> {
        override fun generate(context: GenerationContext): LocalDateTime = LocalDateTime.now()
    }

    class UserIdGenerator : Generator<Long> {
        override fun generate(context: GenerationContext): Long = context.user?.id ?: throw NotGeneratedException()
    }

    class ProvidedIdGenerator : Generator<Long> {
        override fun generate(context: GenerationContext): Long =
            context.prefilledValues["id"] as Long? ?: throw NotGeneratedException()
    }

    class ProvidedNameGenerator : Generator<String> {
        override fun generate(context: GenerationContext): String =
            context.prefilledValues["name"] as String? ?: throw NotGeneratedException()
    }
}
