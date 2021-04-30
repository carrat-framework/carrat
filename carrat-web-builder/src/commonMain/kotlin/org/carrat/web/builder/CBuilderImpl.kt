package org.carrat.web.builder

import kotlinx.html.Tag
import org.carrat.web.fragments.Fragment
import org.carrat.web.fragments.ListFragment
import org.carrat.context.Context
import org.carrat.web.webapi.Element

internal class CBuilderImpl(consumer: CBuilder.() -> Unit, context: Context) : AbstractCBuilderImpl(context) {
    init {
        consumer()
    }

    override fun doAttach(consumer: (Element) -> Unit) {
        throw UnsupportedOperationException("Not inside context of fragment, that has a corresponding element.")
    }

    override fun doOnTagAttributeChange(tag: Tag, attribute: String, value: String?) {
        throw UnsupportedOperationException("Tag attribute can't be changed this way.")//TODO
    }

    override fun finalize(): Fragment = ListFragment(
        children.toTypedArray(),
        onMount.toTypedArray(),
        onUnmount.toTypedArray()
    )
}
