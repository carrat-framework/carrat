package org.carrat.flow

public fun interface Query<in State, out Result> {
    public operator fun invoke(state : State) : Result
}

