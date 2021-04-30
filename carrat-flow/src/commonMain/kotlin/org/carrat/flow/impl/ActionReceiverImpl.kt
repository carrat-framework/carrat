package org.carrat.flow.impl

import org.carrat.flow.ActionReceiver
import org.carrat.flow.FlowImpl
import org.carrat.flow.Observable

internal class ActionReceiverImpl(
    private val flow: FlowImpl
) : ActionReceiver {
    var open = false

    override fun <Mutation> Observable<*, Mutation>.apply(mutation: Mutation) {
        val observable = this@apply
        if (observable is ObservableImpl<*, Mutation> && observable.flow === flow) {
            observable.store.apply(mutation)
            flow.changed.add(observable.store)
        } else {
            throw IllegalArgumentException("This observable is not bound to this flow.")
        }
    }
}
