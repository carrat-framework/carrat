package org.carrat.web.builder

import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import org.carrat.web.fragments.Fragment
import org.carrat.context.Context
import org.carrat.context.HasContext
import org.carrat.experimental.ExperimentalMultipleReceivers
import org.carrat.web.webapi.Element
import org.carrat.web.webapi.EventListener

/**
 * Builder capable of appending to some fragment.
 */
@CBuilderMarker
public sealed interface CFragmentBuilder : TagConsumer<Fragment>, HasContext {
    public override val context: Context

    public fun onMount(callback: () -> Unit)
    public fun onUnmount(callback: () -> Unit)

    @ExperimentalMultipleReceivers
    public operator fun String.unaryPlus() {
        text(this)
    }

    public fun <E : Element>attach(consumer: (E) -> Unit)

    public fun onTagEvent(tag: Tag, event: String, value: EventListener)
}

public fun CFragmentBuilder.text(
    value: String
) {
    onTagContent(value)
}
