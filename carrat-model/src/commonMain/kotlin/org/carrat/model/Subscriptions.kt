package org.carrat.model

import org.carrat.experimental.CarratExperimental

@CarratExperimental
public class Subscriptions<T> : Subscribable<T> {
    private val subscribers: MutableMap<Subscription, Subscriber<T>> = mutableMapOf()

    public val hasSubscribers: Boolean
        get() = subscribers.isNotEmpty()

    override fun subscribe(subscriber: Subscriber<T>): Subscription {
        val subscription = object : Subscription {
            override fun cancel() {
                subscribers.remove(this)
            }
        }
        subscribers[subscription] = subscriber
        return subscription
    }

    public fun emit(value: T) {
        subscribers.forEach { (subscription, subscriber) ->
            subscriber.emit(value, subscription)
        }
    }
}
