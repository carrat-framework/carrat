package org.carrat.flow.impl

import org.carrat.model.Change
import org.carrat.model.Subscriber
import org.carrat.model.Subscription

internal class ChangeSubscriber<Value>(
    var lastValue : Value,
    val delegate : Subscriber<Change<Value>>
) : Subscriber<Value> {
    override fun emit(event: Value, subscription: Subscription) {
        val lastValue = lastValue
        this@ChangeSubscriber.lastValue = event
        with(delegate) {
            emit(Change(lastValue, event), subscription)
        }
    }
}
