package org.carrat.model.list

import org.carrat.experimental.CarratExperimental
import org.carrat.model.Subscribable

@CarratExperimental
public interface ObservableList<out E> : List<E>, Subscribable<List<ListManipulation<E>>> {
}

