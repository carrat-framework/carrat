package org.carrat.web.fragments

import kotlinx.html.TagConsumer
import org.carrat.web.webapi.Element
import org.carrat.web.webapi.Node

public class ListFragment(
    private val children: Array<Fragment>,
    private val onMount: Array<() -> Unit>,
    private val onUnmount: Array<() -> Unit>
) : Fragment() {
    override var attached: Boolean = false

    override fun TagConsumer<*>.render() {
        children.forEach { with(it) { render() } }
    }

    override fun attach() {
        children.forEach { it.attach() }
    }

    override fun mount() {
        children.forEach { it.mount() }
        onMount.forEach { it() }
    }

    override fun unmount() {
        children.forEach { it.unmount() }
        onUnmount.forEach { it() }
    }

    override fun mountDom(parent: Element, after: Node?): Node? {
        var current = after
        children.forEach { current = it.mountDom(parent, current) }
        return current
    }

    override fun unmountDom() {
        children.forEach { it.unmountDom() }
    }

    override fun hydrate(nodeIterator: Iterator<NodeToken>) {
        children.forEach { it.hydrate(nodeIterator) }
        onMount.forEach { it() }
        attached = true
    }
}

internal fun ListFragment(
    children: List<Fragment>
): ListFragment = ListFragment(children.toTypedArray(), emptyArray(), emptyArray())
