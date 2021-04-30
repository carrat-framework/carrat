package org.carrat.flow.impl

import org.carrat.model.Change
import org.carrat.flow.QueryReceiver
import org.carrat.model.Subscriber
import org.carrat.model.Subscription

internal class SubscriptionChangeCall<Value>(
    val oldValue: Value,
    override var newValue: Value,
    private val subscriber: Subscriber<Change<Value>>,
    private val subscription: Subscription
) : SubscriptionCall<Value>() {
    override fun QueryReceiver.invoke() {
        with(subscriber) {
            emit(Change(oldValue, newValue), subscription)
        }
    }
}
