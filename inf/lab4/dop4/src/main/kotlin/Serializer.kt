import element.Element

abstract class Serializer {

    abstract fun serialize(elem: Element, depth: Int = 0): String

}