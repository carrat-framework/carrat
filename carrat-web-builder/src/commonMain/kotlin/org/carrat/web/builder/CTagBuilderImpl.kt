package org.carrat.web.builder

import kotlinx.css.CSSBuilder
import kotlinx.html.Tag
import org.carrat.web.fragments.TagFragment
import org.carrat.context.Context
import org.carrat.experimental.ExperimentalMultipleReceivers
import org.carrat.web.webapi.Element
import org.carrat.web.webapi.EventListener
import org.w3c.dom.events.Event

@OptIn(ExperimentalMultipleReceivers::class)
internal class CTagBuilderImpl<T : Tag>(
    tag : (CTagBuilder<T>)->T,
    private val consumer: CTagBlock<T>,
    context: Context
) : AbstractCBuilderImpl(context),
    AbstractCAnyTagBuilder<T>, CTagBuilder<T> {
    constructor(tagConstructor: TagConstructor<T>, consumer: CTagBlock<T>, context: Context) : this({ builder -> tagConstructor(emptyMap(), builder)}, consumer, context)

    override val tag : T = tag(this)
    override val css: CSSBuilder = CSSBuilder()
    override var element: Element? = null
    private val attach = mutableListOf<(Element)->Unit>({ element = it })
    init {
        consumer()
    }

    override fun onTagAttributeChange(tag: Tag, attribute: String, value: String?) {
        rootOrCurrent(tag, {
            super.onTagAttributeChange(tag, attribute, value)
        }, {
            if(value != null) {
                setAttribute(attribute, value)
            } else {
                removeAttribute(attribute)
            }
        })
    }

    override fun doAttach(consumer: (Element) -> Unit) {
        attach += consumer
    }

    override fun doOnTagAttributeChange(tag: Tag, attribute: String, value: String?) {
        super.doOnTagAttributeChange(tag, attribute, value)
    }

    override fun onTagEvent(tag: Tag, event: String, value: (Event) -> Unit) {
        rootOrCurrent(tag, {
            this@CTagBuilderImpl.attach<Element> {
                @Suppress("UNCHECKED_CAST")
                it.addEventListener(event, value as (org.carrat.web.webapi.Event)->Unit)
            }
        }, {
            @Suppress("UNCHECKED_CAST")
            addEventListener(event, value as (org.carrat.web.webapi.Event)->Unit)
        })
    }

    override fun onTagEvent(tag: Tag, event: String, value: EventListener) {
        rootOrCurrent(tag, {
            this@CTagBuilderImpl.attach<Element> {
                it.addEventListener(event, value)
            }
        }, {
            addEventListener(event, value)
        })
    }

    private inline fun rootOrCurrent(
        tag: Tag,
        current: Tag.() -> Unit,
        root: Element.() -> Unit
    ) {
        val element = element
        if (tag == this.tag && element != null) {
            element.root()
        } else {
            tag.current()
        }
    }

    override fun finalize(): TagFragment<T> {
        tag.applyCss(css)
        return TagFragment(
            tag,
            children.toTypedArray(),
            attach.toTypedArray(),
            onMount.toTypedArray(),
            onUnmount.toTypedArray()
        )
    }
}
