package org.carrat.web.builder.fragment

import org.carrat.web.builder.html.HtmlWriter
import org.carrat.web.webapi.Element
import org.carrat.web.webapi.Node
import org.carrat.web.webapi.Text

public abstract class Fragment internal constructor() {
    internal abstract val attached: Boolean
    public abstract fun HtmlWriter.render()
    internal abstract fun attach()
    internal abstract fun mount()
    internal abstract fun unmount()
    internal abstract fun mountDom(parent: Element, after: Node?): Node?
    internal abstract fun unmountDom()
    internal abstract fun hydrate(nodeIterator: Iterator<NodeToken>)

    public companion object
}

public fun Node.hydrate(fragment: Fragment) {
    fragment.hydrate(ElementIterator(this).asSequence().filter {
        !(it is Text && it.wholeText.isBlank())
    }.mapNotNull { it.asToken() }.iterator())
}

public fun hydrate(fragment: Fragment, element: Element, after : Node? = null, before : Node? = null) {
    fragment.hydrate(ElementIterator(after?.nextSibling, before).asSequence().filter {
        !(it is Text && it.wholeText.isBlank())
    }.mapNotNull { it.asToken() }.iterator())
}

public fun Element.mount(fragment: Fragment, after: Node? = null) {
    fragment.attach()
    fragment.mountDom(this, after)
    fragment.mount()
}

public fun HtmlWriter.render(fragment: Fragment) {
    with(fragment) { render() }
}
