package org.carrat.simple.model

import org.carrat.experimental.CarratExperimental
import org.carrat.model.SimpleSubscriber
import org.carrat.model.Subscribable
import org.carrat.model.Subscription
import org.carrat.model.asSubscriber

@CarratExperimental
public fun <T> Subscribable<T>.subscribe(subscriber: SimpleSubscriber<T>): Subscription = subscribe(subscriber.asSubscriber())
