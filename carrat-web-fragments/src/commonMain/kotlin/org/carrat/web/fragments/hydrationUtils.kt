package org.carrat.web.fragments

import org.carrat.web.webapi.Comment
import org.carrat.web.webapi.Node
import org.carrat.web.webapi.Text

internal inline fun <reified T> Iterator<NodeToken>.requireNext(): T {
    try {
        while (true) {
            when (val next = next()) {
                is T -> return next
                is TextToken -> {
                    continue
                }
                else -> throw UnexpectedNodeException(T::class.simpleName!!, next.node)
            }
        }
    } catch (e: NoSuchElementException) {
        throw IllegalStateException("Expected ${T::class.simpleName}, but end of token stream encountered.")
    }
}

internal inline fun Iterator<NodeToken>.requireMarker(marker: Marker): Comment {
    val next = requireNext<MarkerToken>()
    if (next.marker != marker) {
        throw UnexpectedNodeException(marker, next.element)
    }
    return next.element
}

public class UnexpectedNodeException(
    public val expected: Any,
    public val encountered: Node
) : IllegalStateException("Expected $expected, but ${formatNode(encountered)} encountered.")

private fun formatNode(node: Node): String {
    return when (node) {
        is Text -> "Text(\"${node.wholeText}\")"
        else -> node.toString()
    }
}
