package dev.alllexey.common.model.field

import kotlinx.serialization.Serializable

@Serializable
sealed interface Validator {
    fun validate(value: String): String? // return null to pass, else error description

    @Serializable object NotBlank : Validator {
        override fun validate(value: String): String? {
            if (!value.isBlank()) return null
            return "Значение не может быть пустым"
        }
    }

    @Serializable data class LessThan(val threshold: Double) : Validator {
        override fun validate(value: String): String? {
            if (value.toDouble() < threshold) return null
            return "Значение должно быть меньше $threshold"
        }
    }

    @Serializable data class LessThanOrEqual(val threshold: Double) : Validator {
        override fun validate(value: String): String? {
            if (value.toDouble() <= threshold) return null
            return "Значение должно быть не больше $threshold"
        }
    }

    @Serializable data class GreaterThan(val threshold: Double) : Validator {
        override fun validate(value: String): String? {
            if (value.toDouble() > threshold) return null
            return "Значение должно быть больше $threshold"
        }
    }
    data class GreaterThanOrEqual(val threshold: Double) : Validator {
        override fun validate(value: String): String? {
            if (value.toDouble() >= threshold) return null
            return "Значение должно быть не меньше $threshold"
        }
    }
}