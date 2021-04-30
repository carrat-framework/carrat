package org.carrat.flow.property

import org.carrat.flow.Query
import org.carrat.flow.SetValue
import org.carrat.flow.Store
import org.carrat.model.Subscriber
import org.carrat.model.Subscription
import org.carrat.model.Subscriptions

public class PropertyStore<Value>(
    private var value: Value
) : Store<Value, SetValue<Value>> {
    private val subscriptions = Subscriptions<Value>()
    private var nextValue: Value = value

    public override fun <Result> query(query: Query<Value, Result>): Result = query(value)

    public override fun <Result> subscribe(query: Query<Value, Result>, subscriber: Subscriber<Result>): Subscription =
        subscriptions.subscribe { value, subscription ->
            subscriber.emit(query(value), subscription)
        }

    override fun apply(mutation: SetValue<Value>) {
        nextValue = mutation.value
    }

    override fun flush() {
        if (nextValue != value) {
            value = nextValue
            subscriptions.emit(value)
        }
    }

    override fun subscribeMutations(subscriber: Subscriber<List<SetValue<Value>>>): Subscription {
        throw UnsupportedOperationException("Subscribe mutations is not supported for mapped queries.")
    }
}

