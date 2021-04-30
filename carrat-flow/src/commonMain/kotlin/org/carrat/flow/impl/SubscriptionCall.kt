package org.carrat.flow.impl

import org.carrat.flow.QueryReceiver

internal sealed class SubscriptionCall<Value> {
    abstract var newValue: Value
    abstract fun QueryReceiver.invoke()
}
