package org.carrat.flow.impl

import org.carrat.flow.*
import org.carrat.model.Change
import org.carrat.model.Subscriber
import org.carrat.model.Subscription

internal class QueryReceiverImpl(
    override val flow: FlowImpl,
    private var tracker: LinkedHashSet<Dependency<*>>? = null
) : QueryReceiver {

    override fun <State, Result> Observable<State, *>.query(query: Query<State, Result>): Result =
        accessObservableImpl {
            tracker?.add(Dependency(this, query))
            store.query(query)
        }

    override fun <Value> query(query: FlowQuery<Value>): Value = query.run { invoke() }

    override fun <Value> lazyMap(query: FlowQuery<Value>): Observable<Value, Nothing> {
        return ObservableImpl(flow, MappedObservable(flow, query))
    }

    override fun <State, Value> Observable<State, *>.lazyMap(query: Query<State, Value>): Observable<Value, Nothing> =
        lazyMap(FlowQuery {
            this@lazyMap.query(query)
        })

    override fun <State, Value, Result> Observable<State, *>.lazyMap(
        query: Query<State, Value>,
        transform: (Value) -> Result
    ): Observable<Result, Nothing> = lazyMap {
        transform(this@lazyMap.query(query))
    }

    override fun <State, Value> Observable<State, *>.subscribe(
        query: Query<State, Value>,
        subscriber: Subscriber<Value>
    ): Subscription = accessStore {
        val subscriptionId = SubscriptionId(object {})
        subscribe(query) { newValue, subscription ->
            this@QueryReceiverImpl.flow.deferredSubscriptionCalls[subscriptionId] =
                SubscriptionValueCall(newValue, subscriber, subscription)
        }
    }

    override fun <Value> subscribeChanges(
        query: FlowQuery<Value>,
        subscriber: Subscriber<Change<Value>>
    ): Subscription {
        TODO("Not yet implemented")
    }

    override fun <Mutation> Observable<*, Mutation>.subscribeMutations(subscriber: Subscriber<List<Mutation>>): Subscription = accessStore {
        val subscriptionId = SubscriptionId(object {})
        subscribeMutations { mutations, subscription ->
            @Suppress("UNCHECKED_CAST")
            val pendingMutations =
                this@QueryReceiverImpl.flow.deferredMutationSubscriptionCalls.getOrPut(subscriptionId) { MutationSubscriptionCall(subscriber, subscription) } as MutationSubscriptionCall<Mutation>
            pendingMutations.mutations += mutations
        }
    }

    override fun <State, Value> Observable<State, *>.subscribeChanges(
        query: Query<State, Value>,
        subscriber: Subscriber<Change<Value>>
    ): Subscription = accessStore {
        val subscriptionId = SubscriptionId(object {})
        subscribeChange(query) { change, subscription ->
            @Suppress("UNCHECKED_CAST")
            val subscriptionCall: SubscriptionCall<Value> =
                this@QueryReceiverImpl.flow.deferredSubscriptionCalls.getOrPut(subscriptionId) {
                    SubscriptionChangeCall(change.oldValue, change.newValue, subscriber, subscription)
                } as SubscriptionCall<Value>
            subscriptionCall.newValue = change.newValue
        }
    }

    private fun <State, Mutation, Result> Observable<State, Mutation>.accessObservableImpl(consumer: ObservableImpl<State, Mutation>.() -> Result): Result {
        val observable = this@accessObservableImpl
        if (observable is ObservableImpl<State, Mutation> && observable.flow === this@QueryReceiverImpl.flow) {
            return observable.consumer()
        } else {
            throw IllegalArgumentException("This observable is not bound to this flow.")
        }
    }

    private fun <State, Mutation, Result> Observable<State, Mutation>.accessStore(query: Store<State, Mutation>.() -> Result): Result =
        this.accessObservableImpl {
            store.query()
        }
}
