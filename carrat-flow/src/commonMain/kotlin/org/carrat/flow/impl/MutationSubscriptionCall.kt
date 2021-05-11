package org.carrat.flow.impl

import org.carrat.model.Subscriber
import org.carrat.model.Subscription

internal class MutationSubscriptionCall<Mutation>(
    private val subscriber: Subscriber<List<Mutation>>,
    private val subscription: Subscription
) {
    val mutations: MutableList<Mutation> = mutableListOf()
    fun invoke() {
        subscriber.emit(mutations, subscription)
    }
}
