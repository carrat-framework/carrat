package org.carrat.web.builder.html

import kotlinx.css.CSSBuilder
import kotlinx.css.RuleSet
import org.carrat.context.Context
import org.carrat.model.SubscribableReference
import org.carrat.model.Subscription
import org.carrat.web.builder.Builder
import org.carrat.web.builder.applyCss
import org.carrat.web.webapi.Element
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KMutableProperty1

public interface AnyTagBuilder<out T : TagHandler<E>, out E : Element> : Builder {
    public val tag: T

    public fun attach(consumer: E.() -> Unit)
}

public fun <T : CoreAttributeGroup<*>> AnyTagBuilder<T, *>.css(handler: RuleSet) {
    val css = CSSBuilder()
    css.handler()
    tag.applyCss(css)
}

public inline fun <T : Tag<T, *>> AnyTagBuilder<T, *>.attributes(block: T.() -> Unit) {
    tag.block()
}

public fun <E : Element> AnyTagBuilder<*, E>.attach(property: KMutableProperty0<E>) {
    attach { property.set(this) }
}

public fun <E : Element, V> AnyTagBuilder<Tag<*,E>, E>.bind(
    property: KMutableProperty1<in E, in V>,
    valueReference: SubscribableReference<V>
) {
    var subscription: Subscription? = null
    onMount {
        subscription = valueReference.synchronizeValue { v, _ ->
            property.set(tag.getElement(), v)
        }
    }

    onUnmount {
        subscription!!.cancel()
    }
}

public fun <V> AnyTagBuilder<*, *>.bind(
    property: KMutableProperty0<in V>,
    valueReference: SubscribableReference<V>
) {
    var subscription: Subscription? = null
    onMount {
        subscription = valueReference.synchronizeValue { v, _ ->
            property.set(v)
        }
    }

    onUnmount {
        subscription!!.cancel()
    }
}

public fun <V> AnyTagBuilder<*, *>.bind(
    property: ()->KMutableProperty0<in V>,
    valueReference: SubscribableReference<V>
) {
    var subscription: Subscription? = null
    onMount {
        subscription = valueReference.synchronizeValue { v, _ ->
            property().set(v)
        }
    }

    onUnmount {
        subscription!!.cancel()
    }
}

public fun <T : Tag<T, out E>, E : Element> AnyTagBuilder<T, E>.withContext(context: Context) : AnyTagBuilder<T, E> {
    return object : AnyTagBuilder<T, E> by this {
        override val context: Context
            get() = context
    }
}

public inline fun <T : Tag<T, out E>, E : Element> AnyTagBuilder<T, E>.withContext(context: Context, content : AnyTagBuilder<T, E>.()->Unit) {
    object : AnyTagBuilder<T, E> by this {
        override val context: Context
            get() = context
    }.apply(content)
}

