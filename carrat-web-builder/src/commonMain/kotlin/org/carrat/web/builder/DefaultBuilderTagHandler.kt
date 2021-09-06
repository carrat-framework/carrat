package org.carrat.web.builder

import org.carrat.web.builder.fragment.AbstractTagFragment
import org.carrat.web.builder.html.TagHandler
import org.carrat.web.webapi.Element

internal class DefaultBuilderTagHandler<E: Element> : TagHandler<E> {
    val attributes = mutableMapOf<String, String>()
    lateinit var fragment: AbstractTagFragment<*, E>

    override fun getElement(): E = fragment.element

    override fun getAttribute(attributeName: String): String? = if (::fragment.isInitialized) {
        fragment.tagHandler.getAttribute(attributeName)
    } else {
        attributes[attributeName]
    }

    override fun setAttribute(attributeName: String, value: String?) {
        if (!::fragment.isInitialized) {
            if (value != null) {
                attributes[attributeName] = value
            } else {
                attributes.remove(attributeName)
            }
        } else {
            fragment.tagHandler.setAttribute(attributeName, value)
        }
    }
}