package org.carrat.web.builder.html

import org.carrat.context.Context
import org.carrat.web.webapi.Element

public interface UnsafeTagBuilder<out T : Tag<T, out E>, out E : Element> : AnyTagBuilder<T, E>, Appendable

public fun <T : Tag<T, out E>, E : Element> UnsafeTagBuilder<T, E>.withContext(context: Context) : UnsafeTagBuilder<T, E> {
    return object : UnsafeTagBuilder<T, E> by this {
        override val context: Context
            get() = context
    }
}

public inline fun <T : Tag<T, out E>, E : Element> UnsafeTagBuilder<T, E>.withContext(context: Context, content : UnsafeTagBuilder<T, E>.()->Unit) {
    object : UnsafeTagBuilder<T, E> by this {
        override val context: Context
            get() = context
    }.apply(content)
}
