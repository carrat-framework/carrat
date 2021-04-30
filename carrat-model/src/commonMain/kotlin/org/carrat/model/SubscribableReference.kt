package org.carrat.model

import org.carrat.experimental.CarratExperimental

@CarratExperimental
public interface SubscribableReference<out T> : Subscribable<Change<T>>, Reference<T> {
    @CarratExperimental
    public fun subscribeValue(subscriber: Subscriber<T>): Subscription = subscribe { v, s ->
        subscriber.emit(v.newValue, s)
    }

    //    public fun subscribeValue(subscriber: SimpleSubscriber<T>): Subscription = subscribeValue(subscriber.asSubscriber())
    @CarratExperimental
    public fun synchronizeValue(subscriber: Subscriber<T>): Subscription {
        val subscription = subscribeValue(subscriber)
        subscriber.emit(value, subscription)
        return subscription
    }
//    public fun synchronizeValue(subscriber: SimpleSubscriber<T>): Subscription = synchronizeValue(subscriber.asSubscriber())

    @CarratExperimental
    public fun <U> map(transform: (T) -> U): SubscribableReference<U>

    public companion object
}

//public fun <T, U> SubscribableReference<T>.map(mapping : (T)->U) : SubscribableReference<U> =
//    MappedSubscribableReference(this, mapping)

public fun <T> SubscribableReference.Companion.constant(value : T) : SubscribableReference<T> = Constant(value)