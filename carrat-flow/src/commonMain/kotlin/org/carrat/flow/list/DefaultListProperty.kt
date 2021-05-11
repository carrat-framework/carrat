package org.carrat.flow.list

import org.carrat.experimental.CarratExperimental
import org.carrat.model.Subscriber
import org.carrat.model.Subscription
import org.carrat.model.Subscriptions
import org.carrat.model.list.*

internal class DefaultListProperty<E>(
    private val store: MutableList<E> = ArrayList()
) : AbstractMutableList<E>(), ListProperty<E> {
    private val subscriptions = Subscriptions<List<ListManipulation<E>>>()

    override fun add(index: Int, element: E) {
        apply(Add(index, element))
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        apply(AddAll(index, elements.toList()))
        return elements.isNotEmpty()
    }

    override fun addAll(elements: Collection<E>): Boolean = addAll(size, elements)

    override fun clear() {
        apply(Clear)
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        val removals = elements.map(::indexOf).sortedDescending().takeWhile { it >= 0 }.map(::RemoveAt)
        apply(removals)
        return removals.isNotEmpty()
    }

    override fun removeAt(index: Int): E {
        val element = store[index]
        apply(RemoveAt(index))
        return element
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        val removals =
            store.asSequence().withIndex().filter { !elements.contains(it.value) }.map { it.index }.sortedDescending()
                .map(::RemoveAt).toList()
        apply(removals)
        return removals.isNotEmpty()
    }

    override fun set(index: Int, element: E): E {
        val replaced = store[index]
        apply(Set(index, element))
        return replaced
    }

    override fun apply(manipulations: List<ListManipulation<E>>) {
        manipulations.forEach { it.applyTo(store) }
        subscriptions.emit(manipulations)
    }

    override fun subscribe(subscriber: Subscriber<List<ListManipulation<E>>>): Subscription =
        subscriptions.subscribe(subscriber)

    override val size by store::size

    override fun get(index: Int): E = store.get(index)

    @CarratExperimental
    override fun <U> map(transform: (E) -> U): SubscribableList<U> = MappedSubscribableList(this, transform)
}
