package org.carrat.flow.property

import org.carrat.flow.Flow
import org.carrat.model.Property

public fun <Value> Flow.property(initialValue: Value): Property<Value> =
    observable(PropertyStore(initialValue)).asProperty()
