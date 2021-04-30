package org.carrat.flow.property

import org.carrat.experimental.CarratExperimental
import org.carrat.flow.Flow
import org.carrat.flow.GetValue
import org.carrat.flow.Observable
import org.carrat.flow.SetValue
import org.carrat.model.*
import org.carrat.model.list.*

internal class ObservableProperty<Element>(
    private val flow: Flow,
    private val delegate: Observable<Element, SetValue<Element>>
) : Property<Element> {
    override var value: Element
        get() = flow.query{delegate.query(GetValue())}
        set(value) = flow.apply { delegate.set(value) }

    override fun subscribe(subscriber: Subscriber<Change<Element>>): Subscription = flow.query {
        delegate.subscribeValueChanges(subscriber)
    }

    @CarratExperimental
    override fun <U> map(transform: (Element) -> U): SubscribableReference<U> = flow.run {
        query {
            delegate.lazyMap(GetValue(), transform).asProperty()
        }
    }
}
