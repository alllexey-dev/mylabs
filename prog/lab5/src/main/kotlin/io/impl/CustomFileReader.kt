package dev.alllexey.io.impl

import dev.alllexey.io.FileReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

class CustomFileReader : FileReader {

    override fun read(file: File): String {
        if (!file.exists()) return ""
        val isr = InputStreamReader(FileInputStream(file))
        return isr.readLines().joinToString(separator = "\n")
    }
}