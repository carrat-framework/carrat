package org.carrat.model.list

import org.carrat.experimental.CarratExperimental
import org.carrat.model.Subscribable
import org.carrat.model.SubscribableReference

@CarratExperimental
public interface SubscribableList<out T> : List<T>, Subscribable<List<ListManipulation<T>>> {
    @CarratExperimental
    public fun <U> map(transform: (T) -> U): SubscribableList<U>
}

