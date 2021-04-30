package org.carrat.flow.list

import org.carrat.flow.Query

public sealed interface ListQuery<in Element, out Result> : Query<List<Element>, Result> {
    public object Size : ListQuery<Any?, Int> {
        override fun invoke(state: List<Any?>): Int = state.size
    }

    public class Contains<Element>(
        private val element : Element
    ) : ListQuery<Element, Boolean> {
        override fun invoke(state: List<Element>): Boolean = state.contains(element)
    }

    public class ContainsAll<Element>(
        private val elements : Collection<Element>
    ) : ListQuery<Element, Boolean> {
        override fun invoke(state: List<Element>): Boolean = state.containsAll(elements)
    }

    public class Get<Element>(
        private val index : Int
    ) : ListQuery<Element, Element> {
        override fun invoke(state: List<Element>): Element = state[index]
    }

    public class IndexOf<Element>(
        private val element : Element
    ) : ListQuery<Element, Int> {
        override fun invoke(state: List<Element>): Int = state.indexOf(element)
    }

    public object IsEmpty : ListQuery<Any?, Boolean> {
        override fun invoke(state: List<Any?>): Boolean = state.isEmpty()
    }

    public class LastIndexOf<Element>(
        private val element : Element
    ) : ListQuery<Element, Int> {
        override fun invoke(state: List<Element>): Int = state.lastIndexOf(element)
    }
}
