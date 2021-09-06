package org.carrat.web.builder

import org.carrat.context.Context
import org.carrat.web.builder.fragment.Fragment
import org.carrat.web.builder.fragment.TagFragment
import org.carrat.web.builder.fragment.UnsafeFragment
import org.carrat.web.builder.html.*
import org.carrat.web.webapi.Element

internal class DefaultUnsafeTagBuilder<T : Tag<T, E>, E : Element>(
    context: Context,
    tagType: TagType<T, E>,
    private val block: UnsafeTagBlock<T, E>
) : AbstractBuilder<UnsafeFragment<T, E>>(context), UnsafeTagBuilder<T, E> {
    private val sb = StringBuilder()
    val attachList = mutableListOf<E.() -> Unit>()

    override fun executeBlock() = block()

    private val handler = DefaultBuilderTagHandler<E>()

    override val tag: T = tagType.createTag(handler)

    override fun append(value: Char): UnsafeTagBuilder<T, E> {
        sb.append(value)
        return this
    }

    override fun append(value: CharSequence?): UnsafeTagBuilder<T, E> {
        sb.append(value)
        return this
    }

    override fun append(value: CharSequence?, startIndex: Int, endIndex: Int): UnsafeTagBuilder<T, E> {
        sb.append(value, startIndex, endIndex)
        return this
    }

    override fun doBuild(
        mountList: Array<() -> Unit>,
        unmountList: Array<() -> Unit>
    ): UnsafeFragment<T, E> {
        val fragment = UnsafeFragment(tag, sb.toString(), attachList.toTypedArray(), mountList, unmountList, handler.attributes)
        handler.fragment = fragment
        return fragment
    }

    override fun attach(consumer: E.() -> Unit) {
        attachList += consumer
    }
}