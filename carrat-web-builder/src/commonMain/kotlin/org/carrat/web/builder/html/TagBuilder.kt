package org.carrat.web.builder.html

import org.carrat.context.Context
import org.carrat.web.webapi.Element

public interface TagBuilder<out T : Tag<T, out E>, out E : Element> : AnyTagBuilder<T, E>, TagConsumer<T>

public fun <T : Tag<T, out E>, E : Element> TagBuilder<T, E>.withContext(context: Context): TagBuilder<T, E> {
    return object : TagBuilder<T, E> by this {
        override val context: Context
            get() = context
    }
}

public inline fun <T : Tag<T, out E>, E : Element> TagBuilder<T, E>.withContext(
    context: Context,
    content: TagBuilder<T, E>.() -> Unit
) {
    object : TagBuilder<T, E> by this {
        override val context: Context
            get() = context
    }.apply(content)
}
