package org.carrat.flow

import org.carrat.experimental.ExperimentalMultipleReceivers
import org.carrat.flow.list.ObservableListProperty
import org.carrat.flow.property.ObservableProperty
import org.carrat.flow.property.ObservableSubscribableReference
import org.carrat.model.*
import org.carrat.model.list.ListManipulation
import org.carrat.model.list.ListProperty

@FlowDsl
public interface QueryReceiver {
    public val flow : Flow
    public fun <State, Result> Observable<State, *>.query(query : Query<State, Result>) : Result
    public fun <State, Value> Observable<State, *>.lazyMap(query : Query<State, Value>) : Observable<Value, Nothing>
    @ExperimentalMultipleReceivers
    public fun <State, Value, Result> Observable<State, *>.lazyMap(query : Query<State, Value>, transform : (Value)->Result) : Observable<Result, Nothing>
    public fun <State, Value> Observable<State, *>.subscribe(query : Query<State, Value>, subscriber: Subscriber<Value>) : Subscription
    public fun <State, Value> Observable<State, *>.subscribeChanges(query : Query<State, Value>, subscriber: Subscriber<Change<Value>>) : Subscription
    public fun <Mutation> Observable<*, Mutation>.subscribeMutations(subscriber: Subscriber<List<Mutation>>) : Subscription
    public fun <Value> query(query : FlowQuery<Value>) : Value
    public fun <Value> lazyMap(query : FlowQuery<Value>) : Observable<Value, Nothing>
    public fun <Value> subscribe(query : FlowQuery<Value>, subscriber: Subscriber<Value>) : Subscription = lazyMap(query).subscribe(GetValue(), subscriber)
    public fun <Value> subscribeChanges(query : FlowQuery<Value>, subscriber: Subscriber<Change<Value>>) : Subscription

    @ExperimentalMultipleReceivers
    public fun <Value> Observable<Value, *>.get() : Value = this.query(GetValue())

    @ExperimentalMultipleReceivers
    public fun <Value> Observable<Value, *>.subscribeValue(subscriber: Subscriber<Value>) : Subscription = subscribe(GetValue(), subscriber)
    @ExperimentalMultipleReceivers
    public fun <Value> Observable<Value, *>.subscribeValueChanges(subscriber: Subscriber<Change<Value>>) : Subscription = subscribeChanges(GetValue(), subscriber)


    @ExperimentalMultipleReceivers
    public fun <Element> Observable<List<Element>, ListManipulation<Element>>.asProperty(): ListProperty<Element> =
        ObservableListProperty(flow, this)

    @ExperimentalMultipleReceivers
    public fun <Element> Observable<Element, SetValue<Element>>.asProperty(): Property<Element> =
        ObservableProperty(flow, this)

    @ExperimentalMultipleReceivers
    public fun <Element> Observable<Element, Nothing>.asProperty(): SubscribableReference<Element> =
        ObservableSubscribableReference(flow, this)
}
