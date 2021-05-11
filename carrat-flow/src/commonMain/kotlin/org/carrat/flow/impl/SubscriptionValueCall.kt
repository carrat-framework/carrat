package org.carrat.flow.impl

import org.carrat.flow.QueryReceiver
import org.carrat.model.Subscriber
import org.carrat.model.Subscription

internal class SubscriptionValueCall<Value>(
    override var newValue: Value,
    private val subscriber: Subscriber<Value>,
    private val subscription: Subscription
) : SubscriptionCall<Value>() {
    override fun invoke() {
        subscriber.emit(newValue, subscription)
    }
}
