package org.carrat.flow

import org.carrat.model.Subscriber
import org.carrat.model.Subscription

public interface Store<out State, Mutation> : Stateful<State, Mutation> {
    public fun apply(mutation: Mutation)
    public fun subscribeMutations(subscriber: Subscriber<List<Mutation>>): Subscription

    /**
     * Apply any deferred mutation and invoke deferred subscribers.
     */
    public fun flush()
}
