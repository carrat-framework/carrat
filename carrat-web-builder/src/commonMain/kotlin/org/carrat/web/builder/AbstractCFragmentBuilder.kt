package org.carrat.web.builder

import kotlinx.html.Tag
import org.carrat.context.Context
import org.carrat.web.webapi.Element
import org.carrat.web.webapi.EventListener
import org.w3c.dom.events.Event

internal sealed class AbstractCFragmentBuilder(
    override val context: Context
) : CFragmentBuilder {
    protected val onMount: MutableList<() -> Unit> = arrayListOf()
    protected val onUnmount: MutableList<() -> Unit> = arrayListOf()

    override fun onTagComment(content: CharSequence) {
        //Ignore comments
        //TODO: Should we throw an exception?
    }

    override fun onTagEvent(tag: Tag, event: String, value: (Event) -> Unit) {
        attach<Element> {
            @Suppress("UNCHECKED_CAST")
            it.addEventListener(event, value as (org.carrat.web.webapi.Event) -> Unit)
        }
    }

    override fun onTagEvent(tag: Tag, event: String, value: EventListener) {
        attach<Element> {
            @Suppress("UNCHECKED_CAST")
            it.addEventListener(event, value as (org.carrat.web.webapi.Event) -> Unit)
        }
    }
}
