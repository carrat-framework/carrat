package org.carrat.flow

public fun interface FlowDispatcher {
    public fun dispatch(task : ()->Unit)
}
