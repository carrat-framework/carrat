package org.carrat.web.builder.html

import org.carrat.context.Context
import org.carrat.experimental.CarratExperimental
import org.carrat.model.SubscribableReference
import org.carrat.model.Subscription
import org.carrat.model.list.SubscribableList
import org.carrat.web.builder.Builder
import org.carrat.web.builder.DefaultTagBuilder
import org.carrat.web.builder.DefaultUnsafeTagBuilder
import org.carrat.web.builder.build
import org.carrat.web.builder.fragment.DynamicListFragment
import org.carrat.web.builder.fragment.Fragment
import org.carrat.web.builder.fragment.TextFragment
import org.carrat.web.webapi.Element

public interface TagConsumer<out Consumer> : Builder {
    public fun append(fragment: Fragment)

    public operator fun String.unaryPlus(): Unit = text(this)
    public operator fun Fragment.unaryPlus(): Unit = append(this)
    public operator fun Block<Consumer>.unaryPlus(): Unit = this()
}

public fun TagConsumer<*>.text(content: Appendable.() -> Unit) {
    val sb = StringBuilder()
    sb.content()
    text(sb.toString())
}

public fun TagConsumer<*>.text(content: String) {
    append(TextFragment(content))
}

public fun <T : Tag<T, E>, E : Element> TagConsumer<*>.tag(tagType: TagType<T, E>, block: TagBlock<T, E>): T {
    val builder = DefaultTagBuilder(context, tagType, block)
    append(builder.build())
    return builder.tag
}

public fun <T : Tag<T, E>, E : Element> TagConsumer<*>.unsafeTag(
    tagType: TagType<T, E>,
    block: UnsafeTagBlock<T, E>
): T {
    val builder = DefaultUnsafeTagBuilder(context, tagType, block)
    append(builder.build())
    return builder.tag
}

@CarratExperimental
public fun TagConsumer<*>.dynamicFragment(fragment: SubscribableReference<Fragment>) {
    val dynamicFragment = DynamicListFragment(listOf(fragment.value))
    var subscription: Subscription? = null
    onMount {
        subscription = fragment.synchronizeValue { value, _ -> dynamicFragment.children[0] = value }
    }
    onUnmount {
        subscription!!.cancel()
    }
    append(dynamicFragment)
}

@CarratExperimental
public fun TagConsumer<*>.dynamicFragment(fragments: SubscribableList<Fragment>) {
    val dynamicFragment = DynamicListFragment(fragments)
    var subscription: Subscription? = null
    onMount {
        subscription = fragments.subscribe { mutations, _ ->
            mutations.forEach { it.applyTo(dynamicFragment.children) }
        }
    }
    onUnmount {
        subscription!!.cancel()
    }
    append(dynamicFragment)
}

@CarratExperimental
public fun <C> TagConsumer<C>.dynamic(fragment: SubscribableReference<TagConsumer<C>.() -> Unit>) {
    return dynamicFragment(fragment.map { context.build(it) })
}

@CarratExperimental
public fun <T, C> TagConsumer<C>.dynamic(
    value: SubscribableReference<T>,
    render: TagConsumer<C>.(value: T) -> Unit
) {
    return dynamic(value.map { { render(it) } })
}

@CarratExperimental
public fun <C> TagConsumer<C>.`if`(
    value: SubscribableReference<Boolean>,
    then: TagConsumer<C>.() -> Unit = {},
    `else`: TagConsumer<C>.() -> Unit = {}
) {
    return dynamic(value) {
        if(it) {
            then()
        } else {
            `else`()
        }
    }
}

@CarratExperimental
public fun <T, C> TagConsumer<C>.dynamic(
    list: SubscribableList<T>,
    render: TagConsumer<C>.(value: T) -> Unit
) {
    return dynamicFragment(list.map { context.build<C> { render(it) } })
}

public fun <C> TagConsumer<C>.withContext(context: Context): TagConsumer<C> {
    return object : TagConsumer<C> by this {
        override val context: Context
            get() = context
    }
}

public inline fun <C> TagConsumer<C>.withContext(
    context: Context,
    content: TagConsumer<C>.() -> Unit
) {
    object : TagConsumer<C> by this {
        override val context: Context
            get() = context
    }.apply(content)
}
