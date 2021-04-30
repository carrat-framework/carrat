package org.carrat.web.builder

import org.carrat.experimental.CarratExperimental
import org.carrat.model.SubscribableReference
import org.carrat.model.Subscriber
import org.carrat.model.Subscription
import org.carrat.web.fragments.ListFragment

//public fun CBuilder.clientOnly(delegate: CBlock) {
//    val holder = Constant(ListFragment(arrayOf(), arrayOf(), arrayOf()))
//
//    dynamicFragment(holder)
//}
//
//private class Constant<T>(override val value: T) : SubscribableReference<T> {
//    override fun subscribe(subscriber: Subscriber<Change<T>>): Subscription {
//        return Subscription { }
//    }
//
//    @CarratExperimental
//    override fun <U> map(transform: (T) -> U): SubscribableReference<U> = Constant(transform(value))
//
//}
