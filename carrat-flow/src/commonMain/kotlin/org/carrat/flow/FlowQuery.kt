package org.carrat.flow

import org.carrat.experimental.ShouldUseTypeFunction

@ShouldUseTypeFunction("in BaseState", "out Result<State : BaseState>")
public fun interface FlowQuery<out Result> {
    public operator fun QueryReceiver.invoke() : Result
}
