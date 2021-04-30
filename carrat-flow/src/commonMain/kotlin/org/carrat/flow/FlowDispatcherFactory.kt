package org.carrat.flow

public fun interface FlowDispatcherFactory {
    public fun dispatch(actionReceiver : ActionReceiver, action : ActionReceiver.()->Unit)
}
