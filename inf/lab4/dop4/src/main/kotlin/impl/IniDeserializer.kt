package impl

import Deserializer
import element.Element

class IniDeserializer : Deserializer() {

    override fun deserialize(str: String): Element {
        val lines = str.split('\n')
        var objectContext: String? = null
        val contextElements: MutableMap<String, MutableMap<String, Element>> = mutableMapOf()
        val rootElements = mutableMapOf<String, Element>()

        lines.forEachIndexed { index, line ->
            if (line.isBlank()) return@forEachIndexed
            if (line.startsWith('[') && line.endsWith(']')) {
                objectContext = line.substring(1, line.length - 1)
                return@forEachIndexed
            }
            val splitPos = line.indexOfFirst { it == '=' }
            if (splitPos == -1) throw IllegalArgumentException("Malformed line $index in INI format: \"$line\"")
            val name = line.substring(0, splitPos).trim()
            val value = line.substring(splitPos + 1)
            val primitiveList = deserializePrimitiveList(value)
            val pair = if (primitiveList.value.isEmpty()) {
                null
            } else if (primitiveList.value.size == 1) {
                name to primitiveList.value[0]
            } else {
                name to primitiveList
            }

            if (pair != null) {
                if (objectContext == null) rootElements.put(pair.first, pair.second)
                else contextElements.computeIfAbsent(objectContext) { mutableMapOf() }.put(pair.first, pair.second)
            }
        }

        contextElements.keys.sortedBy { it.length }
            .reversed()
            .forEach { path ->
                val idx = path.indexOfLast { it == '.' }
                val parent = if (idx == -1) null else path.substring(0, idx)
                if (parent != null) contextElements.putIfAbsent(parent, mutableMapOf())
            }

        contextElements.keys.sortedBy { it.length }
            .reversed()
            .forEach { path ->
                val idx = path.indexOfLast { it == '.' }
                val obj = Element.ObjectElement(contextElements[path]!!.toMap())
                val parent = if (idx == -1) null else path.substring(0, idx)
                val name = path.substring(idx + 1)
                if (parent == null) rootElements.put(name, obj)
                else contextElements.computeIfAbsent(parent) { mutableMapOf() }.put(name, obj)
            }


        return Element.ObjectElement(rootElements)
    }
}