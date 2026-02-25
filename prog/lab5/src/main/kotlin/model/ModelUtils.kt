package dev.alllexey.model

import dev.alllexey.App
import dev.alllexey.commands.InsertCommand
import dev.alllexey.commands.ReplaceIfLowerCommand
import dev.alllexey.commands.UpdateCommand
import dev.alllexey.exceptions.NotGeneratedException
import dev.alllexey.annotations.Generator
import dev.alllexey.annotations.Validator
import java.math.BigDecimal
import java.time.LocalDateTime

class NextIdGenerator : Generator<Long> {
    override fun generate(app: App): Long {
        if (app.scriptExecutor.runningCommand is UpdateCommand) {
            if (UpdateCommand.id == null) {
                app.err("Невозможно восстановить ID корабля")
                return 0
            }
            return UpdateCommand.id!!
        }
        return app.nextId()
    }
}

class ProvidedNameGenerator : Generator<String> {
    override fun generate(app: App): String {
        return app.scriptExecutor.runningCommand?.let {
            val name = when (it) {
                InsertCommand -> InsertCommand.nameArg
                UpdateCommand -> UpdateCommand.nameArg
                ReplaceIfLowerCommand -> throw NotGeneratedException()
                else -> null
            }
            if (name == null) {
                app.err("Невозможно восстановить имя корабля")
                return@let ""
            } else {
                return name
            }
        }!!
    }
}

class CurrentDateTimeGenerator : Generator<LocalDateTime> {
    override fun generate(app: App): LocalDateTime = LocalDateTime.now()
}

class GreaterThanZeroValidator : Validator {
    override fun validate(value: String, app: App): String? {
        return if (BigDecimal(value) > BigDecimal.ZERO) null
        else "Значение должно быть больше 0"
    }
}

class NotEmptyValidator : Validator {
    override fun validate(value: String, app: App): String? {
        return if (value.isNotEmpty()) null
        else "Значение не может быть пустым"
    }
}

class Max759Validator : Validator {
    override fun validate(value: String, app: App): String? {
        return if (BigDecimal(value) <= BigDecimal.valueOf(759)) null
        else "Значение не может быть больше 759"
    }
}

class Max1000Validator : Validator {
    override fun validate(value: String, app: App): String? {
        return if (BigDecimal(value) <= BigDecimal.valueOf(1000)) null
        else "Значение не может быть больше 1000"
    }
}

