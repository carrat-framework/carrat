package org.carrat.web.builder

import org.carrat.context.Context
import org.carrat.context.HasContext

@BuilderMarker
public interface Builder : HasContext {
    public fun onMount(callback: () -> Unit)
    public fun onUnmount(callback: () -> Unit)
}

public fun Builder.withContext(context: Context): Builder {
    return object : Builder by this {
        override val context: Context
            get() = context
    }
}

public inline fun Builder.withContext(
    context: Context,
    content: Builder.() -> Unit
) {
    object : Builder by this {
        override val context: Context
            get() = context
    }.apply(content)
}
