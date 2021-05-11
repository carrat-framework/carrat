package org.carrat.flow.list

import org.carrat.flow.Flow
import org.carrat.flow.Observable
import org.carrat.model.Subscriber
import org.carrat.model.Subscription
import org.carrat.model.list.*

internal class ObservableListProperty<Element>(
    private val flow: Flow,
    private val delegate: Observable<List<Element>, ListManipulation<Element>>
) : AbstractMutableList<Element>(), ListProperty<Element> {
    //TODO: Check if delegate is bound to flow.

    override val size: Int
        get() = flow.query {
            delegate.query(ListQuery.Size)
        }

    override fun contains(element: Element): Boolean = flow.query {
        delegate.query(ListQuery.Contains(element))
    }

    override fun containsAll(elements: Collection<Element>): Boolean = flow.query {
        delegate.query(ListQuery.ContainsAll(elements))
    }

    override fun get(index: Int): Element = flow.query {
        delegate.query(ListQuery.Get(index))
    }

    override fun indexOf(element: Element): Int = flow.query {
        delegate.query(ListQuery.IndexOf(element))
    }

    override fun isEmpty(): Boolean = flow.query {
        delegate.query(ListQuery.IsEmpty)
    }

    override fun lastIndexOf(element: Element): Int = flow.query {
        delegate.query(ListQuery.LastIndexOf(element))
    }

    override fun add(element: Element): Boolean {
        add(size, element)
        return true
    }

    override fun add(index: Int, element: Element) {
        apply(Add(index, element))
    }

    override fun addAll(index: Int, elements: Collection<Element>): Boolean {
        apply(AddAll(index, elements.toList()))
        return true
    }

    override fun addAll(elements: Collection<Element>): Boolean {
        addAll(size, elements)
        return true
    }

    override fun clear() {
        apply(Clear)
    }

    override fun remove(element: Element): Boolean {
        val index = indexOf(element)
        return if (index >= 0) {
            apply(RemoveAt(index))
            true
        } else {
            false
        }
    }

    override fun removeAll(elements: Collection<Element>): Boolean {
        return elements.any { remove(it) }
    }

    override fun removeAt(index: Int): Element {
        val element = get(index)
        apply(RemoveAt(index))
        return element
    }

    override fun retainAll(elements: Collection<Element>): Boolean {
        val elementsToRemove = filter { !elements.contains(it) }
        removeAll(elementsToRemove)
        return elementsToRemove.isNotEmpty()
    }

    override fun set(index: Int, element: Element): Element {
        val result = get(index)
        apply(Set(index, element))
        return result
    }

    override fun subscribe(subscriber: Subscriber<List<ListManipulation<Element>>>): Subscription = flow.query {
        delegate.subscribeMutations(subscriber)
    }

    override fun apply(manipulations: List<ListManipulation<Element>>) = flow.apply {
        delegate.apply(manipulations)
    }

    override fun <U> map(transform: (Element) -> U): SubscribableList<U> = MappedSubscribableList(this, transform)
}
