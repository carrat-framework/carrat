package org.carrat.flow

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.carrat.experimental.ExperimentalMultipleReceivers
import org.carrat.experimental.SealedApi
import org.carrat.flow.list.ObservableListProperty
import org.carrat.flow.property.ObservableProperty
import org.carrat.flow.property.ObservableSubscribableReference
import org.carrat.model.Property
import org.carrat.model.SubscribableReference
import org.carrat.model.list.ListManipulation
import org.carrat.model.list.ListProperty

@SealedApi
public sealed interface Flow {
    public fun <Result> query(query: FlowQuery<Result>): Result
    public fun apply(action: Action)

    public fun <State, Mutation> observable(store: Store<State, Mutation>): Observable<State, Mutation>

    @ExperimentalMultipleReceivers
    public fun <Element> Observable<List<Element>, ListManipulation<Element>>.asProperty(): ListProperty<Element> =
        ObservableListProperty(this@Flow, this)

    @ExperimentalMultipleReceivers
    public fun <Element> Observable<Element, SetValue<Element>>.asProperty(): Property<Element> =
        ObservableProperty(this@Flow, this)

    @ExperimentalMultipleReceivers
    public fun <Element> Observable<Element, Nothing>.asProperty(): SubscribableReference<Element> =
        ObservableSubscribableReference(this@Flow, this)
}

public fun Flow(
    flowDispatcher: CoroutineDispatcher = Dispatchers.Default
): Flow = FlowImpl(flowDispatcher)
