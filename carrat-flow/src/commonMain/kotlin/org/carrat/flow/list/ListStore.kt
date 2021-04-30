package org.carrat.flow.list

import org.carrat.flow.Query
import org.carrat.flow.Store
import org.carrat.model.Subscriber
import org.carrat.model.Subscription
import org.carrat.model.list.ListManipulation
import org.carrat.model.list.ListProperty

internal class ListStore<Element>(
    private val store: ListProperty<Element> = DefaultListProperty()
) : Store<List<Element>, ListManipulation<Element>> {
    private val mutations = mutableListOf<ListManipulation<Element>>()

    override fun <Result> query(query: Query<List<Element>, Result>): Result = query(store)

    override fun <Result> subscribe(query: Query<List<Element>, Result>, subscriber: Subscriber<Result>): Subscription =
        store.subscribe { event, subscription ->
            subscriber.emit(query(store), subscription)
        }

    override fun apply(mutation: ListManipulation<Element>) {
        mutations += mutation
    }

    override fun subscribeMutations(subscriber: Subscriber<List<ListManipulation<Element>>>): Subscription =
        store.subscribe(subscriber)

    override fun flush() {
        store.apply(mutations)
        mutations.clear()
    }
}
