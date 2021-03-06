package org.carrat.web.fragments

import kotlinx.html.TagConsumer
import org.carrat.web.webapi.Element
import org.carrat.web.webapi.Node
import org.carrat.web.webapi.document
import org.carrat.web.webapi.removeFromParent

public class TextFragment(
    internal val text: String
) : Fragment() {
    internal lateinit var node: Node
    override val attached: Boolean
        get() = ::node.isInitialized

    override fun TagConsumer<*>.render() {
        onTagComment(Marker.START.string)
        onTagContent(text)
        onTagComment(Marker.END.string)
    }

    override fun attach() {
        node = document.createTextNode(text)
    }

    override fun mount() {
    }

    override fun unmount() {
    }

    override fun mountDom(parent: Element, after: Node?): Node {
        parent.insertAfter(node, after)
        return node
    }

    override fun unmountDom() {
        node.removeFromParent()
    }

    override fun hydrate(nodeIterator: Iterator<NodeToken>) {
        nodeIterator.requireMarker(Marker.START)
        this.node = nodeIterator.requireNext<TextToken>().node
        nodeIterator.requireMarker(Marker.END)
    }
}
