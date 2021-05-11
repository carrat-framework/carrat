package org.carrat.flow.list

import org.carrat.experimental.CarratExperimental
import org.carrat.model.Subscriber
import org.carrat.model.Subscription
import org.carrat.model.Subscriptions
import org.carrat.model.list.ListManipulation
import org.carrat.model.list.SubscribableList

internal class MappedSubscribableList<T, U>(
    private val original: SubscribableList<T>,
    private val transform: (T) -> U
) : AbstractList<U>(), SubscribableList<U> {
    private var originalSubscription: Subscription? = null
    private val subscriptions = Subscriptions<List<ListManipulation<U>>>()
    private val synchronizing: Boolean
        inline get() = cache != null
    private var cache: MutableList<U>? = null

    override val size: Int
        get() = original.size

    override fun get(index: Int): U {
        val cache = cache
        return if (cache != null) {
            cache[index]
        } else {
            transform(original[index])
        }
    }

    override fun subscribe(subscriber: Subscriber<List<ListManipulation<U>>>): Subscription {
        if (!synchronizing) {
            val cache = mutableListOf<U>()
            this.cache = cache
            (original as List<T>).mapTo(cache, transform)
            originalSubscription = original.subscribe { originalMutations, _ ->
                val mutations = originalMutations.map { it.map(transform) }
                subscriptions.emit(mutations)
            }
        }
        return wrapSubscription(subscriptions.subscribe { mutations, s ->
            subscriber.emit(mutations, wrapSubscription(s))
        })
    }

    private fun wrapSubscription(delegate: Subscription): Subscription {
        return object : Subscription {
            var registered = true
            override fun cancel() {
                if (registered) {
                    delegate.cancel()
                    if (!synchronizing) {
                        originalSubscription!!.cancel()
                    }
                    registered = false
                }
            }
        }
    }

    @CarratExperimental
    override fun <S> map(transform: (U) -> S): SubscribableList<S> = MappedSubscribableList(this, transform)
}
