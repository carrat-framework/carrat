package org.carrat.web.fragments

import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import org.carrat.web.webapi.Element
import org.carrat.web.webapi.Node
import org.carrat.web.webapi.removeFromParent

public class TagFragment<out T : Tag>(
    private val tag: T,
    private val children: Array<Fragment>,
    private val attach: Array<(Element) -> Unit>,
    private val onMount: Array<() -> Unit>,
    private val onUnmount: Array<() -> Unit>
) : Fragment() {
    internal lateinit var element: Element
    override val attached: Boolean
        get() = ::element.isInitialized

    override fun TagConsumer<*>.render() {
        onTagStart(tag)
        for (child in children) {
            render(child)
        }
        onTagEnd(tag)
    }

    override fun attach() {
        element = createElement(tag)
        children.forEach { it.attach() }
        attach.forEach { it(element) }
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

    private fun hydrateTag(element: Element) {
        if (!element.tagName.equals(tag.tagName, true)) throw IllegalArgumentException("Tag name do not match")
        val nodeIterator = ElementIterator(element).asSequence().mapNotNull { it.asToken() }.iterator()
        //TODO Should we validate attributes?
        for (fragment in children) {
            fragment.hydrate(nodeIterator)
        }
        this.element = element
        attach.forEach { it(element) }
        onMount.forEach { it() }
    }
}
