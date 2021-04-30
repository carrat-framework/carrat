package org.carrat.web.builder

import kotlinx.html.*
import org.carrat.web.fragments.*
import org.carrat.context.Context

internal fun build(consumer: CBuilder.() -> Unit, context: Context): Fragment {
    return CBuilderImpl(consumer, context).finalize()
}

@Suppress("UNCHECKED_CAST")
internal fun <T : Tag> buildTag(
    tag: TagConstructor<T>,
    block: CTagBlock<T>,
    context: Context
): TagFragment<T> {
    return CTagBuilderImpl(tag, block, context).finalize()
}

@Suppress("UNCHECKED_CAST")
internal fun <T : Tag> buildUnsafe(
    tag: TagConstructor<T>,
    block: CUnsafeTagBlock<T>,
    context: Context
): UnsafeFragment<T> {
    return CUnsafeTagBuilderImpl(tag, block, context).finalize()
}
