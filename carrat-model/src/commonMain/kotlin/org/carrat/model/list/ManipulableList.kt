package org.carrat.model.list

import org.carrat.experimental.CarratExperimental

@CarratExperimental
public interface ManipulableList<E> : MutableList<E> {
    public fun apply(manipulations : List<ListManipulation<E>>)
}

@CarratExperimental
public fun <E> ManipulableList<E>.apply(manipulation : ListManipulation<E>): Unit = apply(listOf(manipulation))
