package org.carrat.web.builder.fragment

import org.carrat.web.webapi.Comment
import org.carrat.web.webapi.Element
import org.carrat.web.webapi.Node

internal sealed class NodeToken {
    abstract val node : Node
}
internal data class MarkerToken(val element: Comment, val marker: Marker) : NodeToken() {
    override val node: Node
        get() = element
}

internal data class ElementToken(val element: Element) : NodeToken() {
    override val node: Node
        get() = element
}

internal data class TextToken(override val node: Node) : NodeToken()

internal fun Node.asToken(): NodeToken? {
    return when (nodeType) {
        Node.COMMENT_NODE -> asMarker(this as Comment)?.let { MarkerToken(this, it) }
        Node.TEXT_NODE -> TextToken(this)
        Node.ELEMENT_NODE -> ElementToken(this as Element)
        else -> null
    }
}
