package org.carrat.web.builder.fragment

import org.carrat.web.builder.html.HtmlWriter
import org.carrat.web.builder.html.Tag
import org.carrat.web.webapi.Element
import org.carrat.web.webapi.Node
import org.carrat.web.webapi.removeFromParent

public class UnsafeFragment<out T : Tag<T, E>, E : Element> internal constructor(
    tag: T,
    private val unsafe: String,
    attach: Array<E.() -> Unit>,
    onMount: Array<() -> Unit>,
    onUnmount: Array<() -> Unit>,
    attributes: MutableMap<String, String>
) : AbstractTagFragment<T, E>(tag, attach, onMount, onUnmount, attributes) {
    override fun HtmlWriter.render() {
        writeTag(tag.tagType, attributes) {
            writeUnsafe {
                append(unsafe)
            }
        }
    }

    override fun attach() {
        element = createElement(tag.tagType, attributes)
        element.innerHTML = unsafe
        attachList.forEach { it(element) }
    }

    override fun mount() {
        onMount.forEach { it() }
    }

    override fun unmount() {
        onUnmount.forEach { it() }
    }

    override fun mountDom(parent: Element, after: Node?): Node {
        parent.insertAfter(element, after)
        return element
    }

    override fun unmountDom() {
        element.removeFromParent()
    }

    @Suppress("UNCHECKED_CAST")
    override fun hydrate(nodeIterator: Iterator<NodeToken>) {
        element = nodeIterator.requireNext<ElementToken>().element as E
        attachList.forEach { it(element) }
        onMount.forEach { it() }
    }
}
