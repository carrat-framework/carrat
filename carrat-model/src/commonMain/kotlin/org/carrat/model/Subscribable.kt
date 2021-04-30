package org.carrat.model

public interface Subscribable<out Event> {
    public fun subscribe(subscriber: Subscriber<Event>): Subscription
}
