package org.carrat.flow.property

import org.carrat.experimental.CarratExperimental
import org.carrat.flow.Flow
import org.carrat.flow.GetValue
import org.carrat.flow.Observable
import org.carrat.model.Change
import org.carrat.model.SubscribableReference
import org.carrat.model.Subscriber
import org.carrat.model.Subscription

internal class ObservableSubscribableReference<Element>(
    private val flow: Flow,
    private val delegate: Observable<Element, Nothing>
) : SubscribableReference<Element> {
    override val value: Element
        get() = flow.query { delegate.query(GetValue()) }

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
