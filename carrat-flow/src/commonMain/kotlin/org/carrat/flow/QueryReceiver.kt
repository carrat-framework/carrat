package org.carrat.flow

import org.carrat.experimental.ExperimentalMultipleReceivers
import org.carrat.model.Change
import org.carrat.model.Subscriber
import org.carrat.model.Subscription

@FlowDsl
public interface QueryReceiver {
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
}
