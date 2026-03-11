package dev.alllexey.server.io

import java.io.File

interface FileReader {
    fun read(file: File): String
}