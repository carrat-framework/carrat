package org.carrat.flow

import org.carrat.model.Subscription

public fun interface FlowSubscriber<in Event> {
    public fun Flow.emit(event : Event, subscription: Subscription)
}
