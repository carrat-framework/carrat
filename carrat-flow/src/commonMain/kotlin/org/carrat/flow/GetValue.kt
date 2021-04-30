package org.carrat.flow

public class GetValue<State> : Query<State, State> {
    override fun invoke(state: State): State = state
}
