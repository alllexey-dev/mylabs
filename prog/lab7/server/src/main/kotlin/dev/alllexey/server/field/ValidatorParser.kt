package dev.alllexey.server.field

import dev.alllexey.common.model.field.Validator

object ValidatorParser {

    fun parseValidators(vararg strings: String): List<Validator> {
        return strings.map { parseValidator(it) }
    }

    fun parseValidator(str: String): Validator {
        try {
            return when {
                str == "not_blank" -> Validator.NotBlank
                str.startsWith(">=") -> Validator.GreaterThanOrEqual(str.drop(2).toDouble())
                str.startsWith("<=") -> Validator.LessThanOrEqual(str.drop(2).toDouble())
                str.startsWith(">") -> Validator.GreaterThan(str.drop(1).toDouble())
                str.startsWith("<") -> Validator.LessThan(str.drop(1).toDouble())

                else -> throw RuntimeException("Validator pattern not found")
            }
        } catch (e: Exception) {
            throw RuntimeException("Could not parse validator: $str", e)
        }
    }
}