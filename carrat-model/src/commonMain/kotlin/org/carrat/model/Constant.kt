package org.carrat.model

import org.carrat.experimental.CarratExperimental

internal class Constant<T>(
    override val value: T
) : SubscribableReference<T> {

    override fun subscribe(subscriber: Subscriber<Change<T>>): Subscription {
        // Never changes
        return Subscription { }
    }

    @CarratExperimental
    override fun <U> map(transform: (T) -> U): SubscribableReference<U> = Constant(transform(value))
}