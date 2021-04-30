package org.carrat.web.builder

import kotlinx.css.CSSBuilder
import kotlinx.html.Tag

/**
 * Builder capable of appending to a fragment, that has a corresponding html element.
 */
public sealed interface CAnyTagBuilder<T : Tag> : CFragmentBuilder {
    public val tag: T
    public val css: CSSBuilder
}

public fun <T : Tag> CAnyTagBuilder<T>.attributes(block: T.() -> Unit) {
    tag.block()
}
