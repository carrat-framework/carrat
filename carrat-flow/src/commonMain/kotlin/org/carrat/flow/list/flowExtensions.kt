package org.carrat.flow.list

import org.carrat.flow.Flow
import org.carrat.model.list.ListProperty

public fun <Element> Flow.list(initialValue : List<Element> = emptyList()): ListProperty<Element> {
    return observable(ListStore(DefaultListProperty(initialValue.toMutableList()))).asProperty()
}

