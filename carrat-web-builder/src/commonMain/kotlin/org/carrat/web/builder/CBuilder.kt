package org.carrat.web.builder

import kotlinx.html.Tag
import kotlinx.html.Unsafe
import org.carrat.web.fragments.DynamicListFragment
import org.carrat.web.fragments.Fragment
import org.carrat.model.SubscribableReference
import org.carrat.model.Subscription
import org.carrat.experimental.CarratExperimental
import org.carrat.experimental.ExperimentalMultipleReceivers

/**
 * Builder capable of appending nested fragments.
 */
public sealed interface CBuilder : CFragmentBuilder {
    public fun append(fragment: Fragment)

    override fun onTagContentUnsafe(block: Unsafe.() -> Unit) {
        TODO("Not allowed. Throw exception. Create help page.")
    }

    public operator fun CBlock.unaryPlus() {
        this()
    }
}

@ExperimentalMultipleReceivers
public fun <T : Tag> CBuilder.tag(
    tag: TagConstructor<T>,
    block: CTagBlock<T>
) {
    val tagFragment = buildTag(tag, block, context)
    append(tagFragment)
}

@ExperimentalMultipleReceivers
public fun <T : Tag> CBuilder.unsafeTag(tag: TagConstructor<T>, block: CUnsafeTagBlock<T>) {
    val unsafeFragment = buildUnsafe(tag, block, context)
    append(unsafeFragment)
}

@CarratExperimental
public fun CBuilder.dynamicFragment(fragment: SubscribableReference<Fragment>) {
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
public fun CBuilder.dynamic(fragment: SubscribableReference<CBuilder.() -> Unit>) {
    return dynamicFragment(fragment.map { context.build(it) })
}

@CarratExperimental
public fun <T> CBuilder.dynamic(
    value: SubscribableReference<T>,
    render: CBuilder.(value: T) -> Unit
) {
    return dynamic(value.map { { render(it) } })
}
