package dev.alllexey.client.network

class PacketFramer {

    private val buffer = StringBuilder()

    fun feed(data: String): List<String> {
        buffer.append(data)
        val messages = mutableListOf<String>()

        while (true) {
            val idx = buffer.indexOf("\n")
            if (idx == -1) break

            val msg = buffer.substring(0, idx)
            buffer.delete(0, idx + 1)
            messages.add(msg)
        }

        return messages
    }
}