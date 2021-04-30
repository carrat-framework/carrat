package org.carrat.flow.impl

import org.carrat.flow.*
import org.carrat.model.Subscription

internal class ObservableImpl<out State, Mutation>(
    internal val flow: FlowImpl,
    internal val store: Store<State, Mutation>
) : Observable<State, Mutation> {
    fun changedGeneration(): Generation {
        return if (store is MappedObservable<*>) {
            store.changedGeneration()
        } else {
            flow.generation
        }
    }
    fun validate() {
        if (store is MappedObservable<*>) {
            store.validate()
        }
    }

    private val dependants = mutableMapOf<Query<*, *>, QueryDependants<*>>()

    fun <Result> addDependent(query: Query<State, Result>, dependant: MappedObservable<*>) {
        dependants.getOrPut(query) {
            QueryDependants(query)
        }.dependants += dependant
    }

    fun removeDependent(query: Query<State, *>, dependant: MappedObservable<*>) {
        val queryDependants = dependants.get(query)!!
        val dependants = queryDependants.dependants
        dependants -= dependant
        if (dependants.isEmpty()) {
            this.dependants.remove(query)
            queryDependants.dispose()
        }
    }

    private inner class QueryDependants<Result>(
        query: Query<State, Result>
    ) {
        val subscription: Subscription
        val dependants: MutableSet<MappedObservable<*>> = mutableSetOf()

        init {
            subscription = store.subscribe(query) { _, _ ->
                dependants.forEach { flow.reevaluationQueue.push(it) }
            }
        }

        fun dispose() {
            subscription.cancel()
        }
    }

}

internal val ObservableImpl<*, *>.depth: Int
    get() {
        return when (this.store) {
            is MappedObservable<*> -> store.lastDepth
            else -> 0
        }
    }
