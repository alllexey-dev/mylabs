package dev.alllexey

fun main() {

    val filenameProvider = {
        System.getenv("LAB_FILENAME") ?: throw RuntimeException("Missing \"LAB_FILENAME\" env variable")
    }

    val context = App(filenameProvider)
    context.run()
}