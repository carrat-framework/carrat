package org.carrat.web.fragments

import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import org.carrat.web.webapi.Element
import org.carrat.web.webapi.Node
import org.carrat.web.webapi.removeFromParent

public class UnsafeFragment<out T : Tag>(
    internal val tag: T,
    private val unsafe: String,
    private val attach: Array<(Element) -> Unit>,
    internal val onMount: Array<() -> Unit>,
    internal val onUnmount: Array<() -> Unit>
) :
    Fragment() {
    internal lateinit var element: Element
    override val attached: Boolean
        get() = ::element.isInitialized

    override fun TagConsumer<*>.render() {
        onTagStart(tag)
        onTagContentUnsafe {
            +unsafe
        }
        onTagEnd(tag)
    }

    override fun attach() {
        element = createElement(tag)
        element.innerHTML = unsafe
        attach.forEach { it(element) }
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

    override fun hydrate(nodeIterator: Iterator<NodeToken>) {
        element = nodeIterator.requireNext<ElementToken>().element
        attach.forEach { it(element) }
        onMount.forEach { it() }
    }
}
