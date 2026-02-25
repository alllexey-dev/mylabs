package dev.alllexey.exceptions

import java.io.File

class ScriptInputException(
    val file: File,
    val lineNumber: Int,
    override val message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)