package org.carrat.web.builder

import org.carrat.web.webapi.Element
import org.w3c.dom.events.Event

@Suppress("NOTHING_TO_INLINE")
internal actual inline fun Element.setEvent(name: String, noinline callback: (Event) -> Unit) = clientOnly()
