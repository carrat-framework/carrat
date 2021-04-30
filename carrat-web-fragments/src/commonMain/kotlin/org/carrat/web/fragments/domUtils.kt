package org.carrat.web.fragments

import kotlinx.html.Tag
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
    private val parent : Node,
    private var next : Node?,
    private val until : Node? = null
) : Iterator<Node> {
        constructor(element: Node) : this(element, element.firstChild)

    override fun hasNext(): Boolean {
        return next != until
    }

    override fun next(): Node {
        if(hasNext()) {
            val current = next!!
            next = current.nextSibling
            return current
        } else {
            throw NoSuchElementException()
        }
    }
}

internal fun createElement(tag: Tag): Element {
    val element = when {
        tag.namespace != null -> document.createElementNS(tag.namespace!!, tag.tagName)
        else -> document.createElement(tag.tagName)
    }
    writeAttributes(element, tag.attributes)
    return element
}

private fun writeAttributes(element: Element, attributes: Map<String, String>) {
    attributes.forEach {
        element.setAttribute(it.key, it.value)
    }
}
