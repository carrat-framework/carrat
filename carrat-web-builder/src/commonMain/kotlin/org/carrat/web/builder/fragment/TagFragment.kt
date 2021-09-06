package org.carrat.web.builder.fragment

import org.carrat.web.builder.html.HtmlWriter
import org.carrat.web.builder.html.Tag
import org.carrat.web.webapi.Element
import org.carrat.web.webapi.Node
import org.carrat.web.webapi.removeFromParent

public class TagFragment<out T : Tag<T, E>, E : Element>(
    tag: T,
    private val children: Array<Fragment>,
    attach: Array<E.() -> Unit>,
    onMount: Array<() -> Unit>,
    onUnmount: Array<() -> Unit>,
    attributes: MutableMap<String, String>
) : AbstractTagFragment<T, E>(tag, attach, onMount, onUnmount, attributes) {
    override fun HtmlWriter.render() {
        writeTag(tag.tagType, attributes) {
            for (child in children) {
                render(child)
            }
        }
    }

    override fun attach() {
        element = createElement(tag.tagType, attributes)
        children.forEach { it.attach() }
        attachList.forEach { it(element) }
    }

    override fun mount() {
        children.forEach { it.mount() }
        onMount.forEach { it() }
    }

    override fun mountDom(parent: Element, after: Node?): Node {
        var ls: Node? = null
        children.forEach { ls = it.mountDom(element, ls) }
        parent.insertAfter(element, after)
        return element
    }

    override fun unmount() {
        onUnmount.forEach { it() }
        children.forEach { it.unmount() }
    }

    override fun unmountDom() {
        element.removeFromParent()
    }

    override fun hydrate(nodeIterator: Iterator<NodeToken>) {
        hydrateTag(nodeIterator.requireNext<ElementToken>().element)
    }

    @Suppress("UNCHECKED_CAST")
    private fun hydrateTag(element: Element) {
        if (!element.tagName.equals(tag.tagType.name, true)) throw IllegalArgumentException("Tag name do not match")
        val nodeIterator = ElementIterator(element).asSequence().mapNotNull { it.asToken() }.iterator()
        //TODO Should we validate attributes?
        for (fragment in children) {
            fragment.hydrate(nodeIterator)
        }
        element as E
        this.element = element
        attachList.forEach { it(element) }
        onMount.forEach { it() }
    }
}
