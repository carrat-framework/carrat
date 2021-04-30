package org.carrat.model

public fun interface Subscriber<in Event> {
    public fun emit(event : Event, subscription: Subscription)
}
