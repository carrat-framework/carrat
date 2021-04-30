package org.carrat.web.builder

import kotlinx.css.RuleSet
import kotlinx.html.Tag
import org.carrat.experimental.CarratExperimental
import org.carrat.experimental.ExperimentalMultipleReceivers
import org.carrat.model.SubscribableReference
import org.carrat.model.Subscription
import org.carrat.web.webapi.Element
import kotlin.jvm.JvmName
import kotlin.reflect.KMutableProperty1

public sealed interface CTagBuilder<T : Tag> : CBuilder, CAnyTagBuilder<T> {
}

@CarratExperimental
public inline fun CTagBuilder<*>.css(handler: RuleSet): Unit = css.handler()

@ExperimentalMultipleReceivers("CTagBuilder", "T")
public fun <E : Element, V> CAnyTagBuilder<*>.bind(
    property: KMutableProperty1<in E, in V>,
    valueReference: SubscribableReference<V>
) {
//    tag.attributes[property.name] = valueReference.value.toString()//TODO: Doesn't seem right.

    var subscription: Subscription? = null
    lateinit var element: E
    attach<E> { element = it }
    onMount {
        subscription = valueReference.synchronizeValue { v, _ ->
            property.set(element, v)
        }
    }

    onUnmount {
        subscription!!.cancel()
    }
}

@JvmName("bind2")
@ExperimentalMultipleReceivers("CTagBuilder", "T")
public fun <E : Element> CAnyTagBuilder<*>.bind(
    property: String,
    valueReference: SubscribableReference<String?>
) {
    val value = valueReference.value
    if (value != null) {
        tag.attributes[property] = value
    } else {
        tag.attributes.remove(property)
    }

    var subscription: Subscription? = null
    lateinit var element: E
    attach<E> { element = it }
    onMount {
        subscription = valueReference.synchronizeValue { value, _ ->
            if (value != null) {
                element.setAttribute(property, value)
            } else {
                element.removeAttribute(property)
            }
        }
    }

    onUnmount {
        subscription!!.cancel()
    }
}

@JvmName("bind3")
@ExperimentalMultipleReceivers("CTagBuilder", "T")
public fun <T : Tag, V> CAnyTagBuilder<T>.bind(
    property: KMutableProperty1<in T, in V>,
    valueReference: SubscribableReference<V>
) {
    property.set(tag, valueReference.value)

    var subscription: Subscription? = null
    onMount {
        subscription = valueReference.synchronizeValue { v, _ ->
            property.set(tag, v)
        }
    }

    onUnmount {
        subscription!!.cancel()
    }
}
