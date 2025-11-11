package impl

import Serializer
import element.Element
import kotlin.collections.component1
import kotlin.collections.component2

class XmlSerializer(
    val prettyPrinting: Boolean = true,
) : Serializer() {

    companion object {
        const val LIST_ITEM_TAG = "item"
    }

    override fun serialize(elem: Element, depth: Int): String {
        return when (elem) {
            is Element.StringElement -> elem.value
            is Element.BoolElement -> elem.value.toString()
            is Element.IntElement -> elem.value.toString()
            is Element.LongElement -> elem.value.toString()
            is Element.FloatElement -> elem.value.toString()
            is Element.DoubleElement -> elem.value.toString()
            is Element.ListElement -> serializeList(elem, depth)
            is Element.ObjectElement -> serializeObject(elem, depth)
        }
    }

    fun serializeList(element: Element.ListElement, depth: Int = 0): String {
        return element.value
            .map { serialize(it, depth + 1) }
            .map { "<$LIST_ITEM_TAG>$it</$LIST_ITEM_TAG>" }
            .joinToString(
                separator = if (prettyPrinting) "\n" else "",
            ) { it }
            .let {
                wrap(it, "", "")
            }
    }

    fun serializeObject(element: Element.ObjectElement, depth: Int = 0): String {
        return element.elements
            .map { (name, value) ->
                "<$name>${serialize(value, depth + 1)}</$name>"
            }.joinToString(
                separator = if (prettyPrinting) "\n" else "",
            )
            .let {
                if (depth > 0) wrap(it, "", "") else it
            }
    }

    fun wrap(str: String, prefix: String, suffix: String): String {
        return if (prettyPrinting) "$prefix\n${increaseIndentation(str)}\n$suffix"
        else "$prefix$str$suffix"
    }

    fun increaseIndentation(str: String): String {
        return str.lines().joinToString(separator = "\n") { l -> "    $l" }
    }
}