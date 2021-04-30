package org.carrat.flow.impl

import org.carrat.flow.*
import org.carrat.model.Subscriber
import org.carrat.model.Subscription

internal class MappedQuery<State, Value>(
    val store : Store<State, Nothing>,
    val query : Query<State, Value>
) : Store<Value, Nothing> {
    override fun <Result> query(query: Query<Value, Result>): Result {
        return query(store.query(this.query))
    }

    override fun <Result> subscribe(
        query: Query<Value, Result>,
        subscriber: Subscriber<Result>
    ): Subscription {
        return store.subscribe(this.query) { value, subscription ->
            subscriber.emit(query(value), subscription)
        }
    }

    override fun apply(mutation: Nothing) {}
    override fun subscribeMutations(subscriber: Subscriber<List<Nothing>>): Subscription {
        throw UnsupportedOperationException("Subscribe mutations is not supported for mapped queries.")
    }

    override fun flush() {}
}
