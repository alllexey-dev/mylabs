package dev.alllexey.io

import java.io.File

interface FileWriter {
    fun write(file: File, string: String)
}