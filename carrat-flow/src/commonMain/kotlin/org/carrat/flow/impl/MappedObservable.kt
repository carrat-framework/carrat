package org.carrat.flow.impl

import org.carrat.flow.*
import org.carrat.model.Subscriber
import org.carrat.model.Subscription
import org.carrat.model.Subscriptions

internal class MappedObservable<Value>(
    private val flow: FlowImpl,
    private val query: FlowQuery<Value>
) : Store<Value, Nothing> {
    private var state: InternalState<Value> = evaluate()
    private var changedGeneration: Generation = flow.generation
    private var reevaluatedGeneration: Generation = flow.generation
    private val subscriptions = Subscriptions<Value>()
    private val synchronizing: Boolean
        get() = subscriptions.hasSubscribers
    val lastDepth: Int
        get() {
            return state.depth
        }

    override fun <Result> query(query: Query<Value, Result>): Result {
        flow.reevaluate(lastDepth)
        validate()
        return query(state.state)
    }

    override fun apply(mutation: Nothing) {}

    override fun <Result> subscribe(query: Query<Value, Result>, subscriber: Subscriber<Result>): Subscription {
        if (!synchronizing) {
            reevaluate()
            state.dependencies.forEach {
                it.register(this)
            }
        }

        return wrapSubscription(subscriptions.subscribe { _, subscription ->
            subscriber.apply { emit(query(state.state), wrapSubscription(subscription)) }
        })
    }

    private fun wrapSubscription(delegate: Subscription): Subscription {
        return object : Subscription {
            var registered = true
            override fun cancel() {
                if(registered) {
                    delegate.cancel()
                    if (!synchronizing) {
                        state.dependencies.forEach {
                            it.unregister(this@MappedObservable)
                        }
                    }
                    registered = false
                }
            }
        }
    }

    private fun evaluate(): InternalState<Value> {
        val tracker = LinkedHashSet<Dependency<*>>()
        val state = flow.withQueryReceiver(QueryReceiverImpl(flow, tracker)) { query.run { invoke() } }
        return InternalState(state, tracker, tracker.maxOf { it.observable.depth })
    }

    internal fun validate() {
        if(!synchronizing) {
            reevaluate()
        }
    }

    internal fun reevaluate() {
        if(reevaluatedGeneration < flow.generation) {
            state.dependencies.forEach { it.observable.validate() }
            if (state.dependencies.any { it.observable.changedGeneration() > reevaluatedGeneration }) {
                val newState = evaluate()
                if (synchronizing) {
                    (state.dependencies - newState.dependencies).forEach {
                        it.unregister(this@MappedObservable)
                    }
                    (newState.dependencies - state.dependencies).forEach {
                        it.register(this@MappedObservable)
                    }
                }
                val changed = newState != state
                if (changed) {
                    val oldState = state
                    state = newState
                    if(oldState.state != newState.state) {
                        changedGeneration = flow.generation
                        subscriptions.emit(newState.state)
                    }
                }
            }
            reevaluatedGeneration = flow.generation
        }
    }

    fun changedGeneration(): Generation {
        return changedGeneration
    }

    override fun subscribeMutations(subscriber: Subscriber<List<Nothing>>): Subscription {
        throw UnsupportedOperationException("Subscribe mutations is not supported for mapped queries.")
    }

    override fun flush() {}
}

private data class InternalState<State>(
    val state: State,
    val dependencies: Set<Dependency<*>>,
    val depth: Int
)

internal data class Dependency<State>(
    val observable: ObservableImpl<State, *>,
    val query: Query<State, *>
) {
    fun register(mappedObservable: MappedObservable<*>) {
        observable.addDependent(query, mappedObservable)
    }

    fun unregister(mappedObservable: MappedObservable<*>) {
        observable.removeDependent(query, mappedObservable)
    }
}
