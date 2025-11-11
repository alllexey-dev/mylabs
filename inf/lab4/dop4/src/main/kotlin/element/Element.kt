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
    fun asBool(): Boolean = (this as? BoolElement)?.value ?: asString().toBoolean()
    fun asInt(): Int = (this as? IntElement)?.value ?: (this as? LongElement)?.value?.toInt() ?: asString().toInt()
    fun asLong(): Long = (this as? LongElement)?.value ?: (this as? IntElement)?.value?.toLong() ?: asString().toLong()
    fun asDouble(): Double = (this as? DoubleElement)?.value ?: (this as? FloatElement)?.value?.toDouble()
    ?: (this as? LongElement)?.value?.toDouble() ?: asString().toDouble()
    fun asFloat(): Float = (this as? FloatElement)?.value ?: (this as? DoubleElement)?.value?.toFloat()
    ?: (this as? LongElement)?.value?.toFloat() ?: asString().toFloat()
}