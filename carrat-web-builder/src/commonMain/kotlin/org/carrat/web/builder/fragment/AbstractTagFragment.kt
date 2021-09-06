package org.carrat.web.builder.fragment

import org.carrat.web.builder.html.Tag
import org.carrat.web.builder.html.TagHandler
import org.carrat.web.webapi.Element
import org.carrat.web.webapi.removeFromParent

public abstract class AbstractTagFragment<out T : Tag<T, E>, E : Element>(
    public val tag: T,
    protected val attachList: Array<E.() -> Unit>,
    protected val onMount: Array<() -> Unit>,
    protected val onUnmount: Array<() -> Unit>,
    protected val attributes: MutableMap<String, String>
) : Fragment() {
    internal val tagHandler = object : TagHandler<E> {
        override fun getElement(): E = element

        override fun getAttribute(attributeName: String): String? = if (::element.isInitialized) {
            element.getAttribute(attributeName)
        } else {
            attributes[attributeName]
        }

        override fun setAttribute(attributeName: String, value: String?) {
            if (::element.isInitialized) {
                if (value != null) {
                    element.setAttribute(attributeName, value)
                } else {
                    element.removeAttribute(attributeName)
                }
            } else {
                if (value != null) {
                    attributes[attributeName] = value
                } else {
                    attributes.remove(attributeName)
                }
            }
        }
    }

    internal lateinit var element: E
    override val attached: Boolean
        get() = ::element.isInitialized

    override fun unmountDom() {
        element.removeFromParent()
    }
}
