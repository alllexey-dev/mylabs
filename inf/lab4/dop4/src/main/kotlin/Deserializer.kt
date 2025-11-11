import element.Element

abstract class Deserializer {
    abstract fun deserialize(str: String): Element

    fun deserializePrimitive(s: String): Element {
        if (s.startsWith('"') && s.endsWith('"')) {
            return Element.StringElement(s.substring(1, s.length - 1))
        }
        return s.toBooleanStrictOrNull()?.let { Element.BoolElement(it) }
            ?: s.toLongOrNull()?.let { Element.LongElement(it) }
            ?: s.toDoubleOrNull()?.let { Element.DoubleElement(it) }
            ?: Element.StringElement(s)
    }

    fun deserializePrimitiveList(s: String): Element.ListElement {
        val elements = mutableListOf<Element>()
        if (s.isBlank()) {
            return Element.ListElement(elements)
        }

        val currentElement = StringBuilder()
        var inQuotes = false

        for (char in s) {
            when (char) {
                '"' -> {
                    inQuotes = !inQuotes
                    currentElement.append(char)
                }
                ',' -> {
                    if (inQuotes) {
                        currentElement.append(char)
                    } else {
                        elements.add(deserializePrimitive(currentElement.toString().trim()))
                        currentElement.clear()
                    }
                }
                else -> {
                    currentElement.append(char)
                }
            }
        }

        if (currentElement.isNotEmpty()) {
            elements.add(deserializePrimitive(currentElement.toString().trim()))
        }

        return Element.ListElement(elements)
    }
}