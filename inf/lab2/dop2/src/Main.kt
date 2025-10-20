fun main() {
    while (true) {
        print("Enter binary message of length 7: ")
        val input = readlnOrNull() ?: break
        if (input.equals("exit", ignoreCase = true)) break

        try {
            println(handle(input))
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
}

fun handle(input: String): String {
    if (input.length != 7) throw IllegalArgumentException("Invalid input length")
    if (input.any { c -> c != '1' && c != '0' }) throw IllegalArgumentException("Invalid input characters")
    val bits = input.toCharArray().map { it - '0' }
    val (r1, r2, r3) = arrayOf(bits[0], bits[1], bits[3])
    val (i1, i2, i3, i4) = arrayOf(bits[2], bits[4], bits[5], bits[6])
    val s1 = r1 xor i1 xor i2 xor i4
    val s2 = r2 xor i1 xor i3 xor i4
    val s3 = r3 xor i2 xor i3 xor i4
    val s = "$s1$s2$s3"
    val bitIndex = when (s) {
        "000" -> -1
        "001" -> 3
        "010" -> 1
        "011" -> 5
        "100" -> 0
        "101" -> 4
        "110" -> 2
        "111" -> 6
        else -> throw RuntimeException("what the...")
    }
    if (bitIndex == -1) return "Message is valid"
    return "Error in bit '$bitIndex', fixed message: ${switchBit(input, bitIndex)}"
}

fun switchBit(input: String, index: Int): String {
    val newVal = if (input[index] == '1') "0" else "1"
    return input.replaceRange(index, index + 1, newVal)
}