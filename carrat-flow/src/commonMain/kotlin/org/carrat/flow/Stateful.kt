package org.carrat.flow

import org.carrat.flow.impl.ChangeSubscriber
import org.carrat.model.Change
import org.carrat.model.Subscriber
import org.carrat.model.Subscription

public sealed interface Stateful<out State, Mutation> {
    public fun <Result> query(query: Query<State, Result>): Result

    public fun <Result> subscribe(query: Query<State, Result>, subscriber: Subscriber<Result>): Subscription
    public fun <Result> subscribeChange(
        query: Query<State, Result>,
        subscriber: Subscriber<Change<Result>>
    ): Subscription =
        subscribe(query, ChangeSubscriber(query(query), subscriber))
}
