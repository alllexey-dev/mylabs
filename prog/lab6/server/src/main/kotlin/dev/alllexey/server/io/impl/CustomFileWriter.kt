package dev.alllexey.server.io.impl

import dev.alllexey.server.io.FileWriter
import java.io.BufferedWriter
import java.io.File

class CustomFileWriter : FileWriter {

    override fun write(file: File, string: String) {
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            file.createNewFile()
        }
        val bw = BufferedWriter(java.io.FileWriter(file))
        bw.write(string)
        bw.flush()
        bw.close()
    }
}