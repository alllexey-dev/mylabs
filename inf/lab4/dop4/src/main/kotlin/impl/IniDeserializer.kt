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
                objectContext = line.substring(1, line.length - 1).trim()
                if (!isValidName(objectContext)) throw IllegalArgumentException("Malformed section name at line $index: \"$line\"")
                return@forEachIndexed
            }
            val splitPos = line.indexOfFirst { it == '=' }
            if (splitPos == -1) throw IllegalArgumentException("Malformed line $index in INI format: \"$line\"")
            val name = line.substring(0, splitPos).trim()
            if (!isValidName(name)) throw IllegalArgumentException("Malformed variable name at line $index in INI format: \"$line\"")
            val appendContext = if (name.contains(".")) "." + name.substring(0, name.indexOfLast { it == '.' }) else ""
            val actualContext = objectContext?.let { it + appendContext }
            val fieldName = if (name.contains(".")) name.substring(name.indexOfLast { it == '.' } + 1) else name
            val value = line.substring(splitPos + 1)
            val primitiveList = deserializePrimitiveList(value)
            val pair = if (primitiveList.value.isEmpty()) {
                null
            } else if (primitiveList.value.size == 1) {
                fieldName to primitiveList.value[0]
            } else {
                fieldName to primitiveList
            }

            if (pair != null) {
                val map = if (actualContext == null) rootElements
                else contextElements.computeIfAbsent(actualContext) { mutableMapOf() }
                if (map.containsKey(pair.first)) {
                    println("WARN: Duplicate key ${pair.first} in ${actualContext ?: "root"}")
                }
                map[pair.first] = pair.second
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

    private fun isValidName(name: String): Boolean {
        return !(name.contains(" ") || name.endsWith(".") || name.startsWith("."))
    }
}