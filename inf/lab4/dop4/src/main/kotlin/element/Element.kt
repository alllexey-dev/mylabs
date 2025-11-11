package element

sealed class Element {
    data class StringElement(val value: String) : Element() {
        override fun toString() = value
    }
    data class BoolElement(val value: Boolean) : Element() {
        override fun toString() = value.toString()
    }
    data class IntElement(val value: Int) : Element() {
        override fun toString() = value.toString()
    }
    data class LongElement(val value: Long) : Element() {
        override fun toString() = value.toString()
    }
    data class FloatElement(val value: Float) : Element() {
        override fun toString() = value.toString()
    }
    data class DoubleElement(val value: Double) : Element() {
        override fun toString() = value.toString()
    }
    data class ListElement(val value: List<Element>) : Element() {
        override fun toString() = value.toString()
    }
    data class ObjectElement(val elements: Map<String, Element>) : Element() {
        override fun toString() = elements.toString()
    }

    fun asString(): String = this.toString()
    fun asBool(): Boolean = asString().toBoolean()
    fun asInt(): Int = asString().toInt()
    fun asLong(): Long = asString().toLong()
    fun asDouble(): Double = asString().toDouble()
    fun asFloat(): Float = asString().toFloat()
}