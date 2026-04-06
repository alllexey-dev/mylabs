package dev.alllexey.common.io

import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import kotlin.io.readText
import kotlin.text.split

interface InputProvider {

    fun readLine(): String

    fun hasNextLine(): Boolean

    fun lineNumber(): Int

    object CONSOLE : InputProvider {
        private var line = 0
        override fun readLine(): String {
            line++
            return readln()
        }
        override fun hasNextLine(): Boolean = true
        override fun lineNumber(): Int = line
    }

    class FileInputProvider(file: File) : InputProvider {
        private val isr = InputStreamReader(FileInputStream(file))
        private val lines = isr.readText().split("\n")
        private var line = 0
        override fun readLine(): String {
            line++
            return lines[line - 1]
        }
        override fun hasNextLine(): Boolean = line < lines.size
        override fun lineNumber(): Int = line
    }
}