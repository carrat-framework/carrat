package org.carrat.flow

import org.carrat.experimental.ExperimentalMultipleReceivers

@FlowDsl
public interface ActionReceiver {
    public fun <Mutation> Observable<*,Mutation>.apply(mutation : Mutation)

    @ExperimentalMultipleReceivers
    public fun <Value> Observable<Value, SetValue<Value>>.set(value : Value) {
        this.apply(SetValue(value))
    }

    @ExperimentalMultipleReceivers
    public fun <Mutation> Observable<*,Mutation>.apply(mutations : List<Mutation>) {
        mutations.forEach { apply(it) }
    }
}
