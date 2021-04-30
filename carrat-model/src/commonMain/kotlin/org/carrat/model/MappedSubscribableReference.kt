package org.carrat.model

//internal class MappedSubscribableReference<T, U>(
//    private val delegate: SubscribableReference<U>,
//    private val mapping: (U) -> T
//) : SubscribableReference<T> {
//    private var _value: T = mapping(delegate.value)
//    private val subscriptions =
//        Subscriptions<ValueChangeEvent<T>>()
//    private var subscription: Subscription? = null
//
//    private val synchronizing: Boolean
//        get() = subscriptions.hasSubscribers
//
//    override val value: T
//        get() = if (synchronizing) {
//            _value
//        } else {
//            mapping(delegate.value)
//        }
//
//    override fun subscribe(subscriber: Subscriber<ValueChangeEvent<T>>): Subscription {
//        if (!synchronizing) {
//            subscription = delegate.subscribeValue { newValue, _ ->
//                val oldValue = _value
//                _value = mapping(newValue)
//                subscriptions.emit(
//                    ValueChangeEvent(
//                        oldValue,
//                        _value
//                    )
//                )
//            }
//        }
//        return wrapSubscription(subscriptions.subscribe { vce, s ->
//            subscriber.emit(vce, wrapSubscription(s))
//        })
//    }
//
//    private fun wrapSubscription(delegate: Subscription): Subscription {
//        return Subscription{
//            delegate.cancel()
//            if (!synchronizing) {
//                this.subscription!!.cancel()
//            }
//        }
//    }
//}
