package org.carrat.model.list

import org.carrat.experimental.CarratExperimental

@CarratExperimental
public interface ListProperty<E> : ManipulableList<E>, ObservableList<E>
