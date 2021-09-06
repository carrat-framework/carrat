package org.carrat.web.builder.fragment

import org.carrat.web.builder.html.Tag
import org.carrat.web.builder.html.TagType
import org.carrat.web.webapi.Element
import org.carrat.web.webapi.Node
import org.carrat.web.webapi.document

internal fun Node.insertAfter(node: Node, after: Node?): Node {
    return if (after != null) {
        insertBefore(node, after.nextSibling)
    } else {
        insertBefore(node, firstChild)
    }
}

internal class ElementIterator(
    private var next: Node?,
    private val until: Node?
) : Iterator<Node> {
    constructor(element: Node) : this(element.firstChild, null)

    override fun hasNext(): Boolean {
        return next != until
    }

    override fun next(): Node {
        if (hasNext()) {
            val current = next!!
            next = current.nextSibling
            return current
        } else {
            throw NoSuchElementException()
        }
    }
}

@Suppress("UNCHECKED_CAST")
internal fun <E : Element> createElement(tagType: TagType<Tag<*, E>, E>, attributes: Map<String, String>): E {
    val element = document.createElement(tagType.name) as E
    writeAttributes(element, attributes)
    return element
}

private fun writeAttributes(element: Element, attributes: Map<String, String>) {
    attributes.forEach {
        element.setAttribute(it.key, it.value)
    }
}
