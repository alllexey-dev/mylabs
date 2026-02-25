package dev.alllexey

fun main() {

    val filenameProvider = {
        System.getenv("LAB_FILENAME") ?: throw RuntimeException("Missing filename env variable")
    }

    val context = App(filenameProvider)
    context.run()
}