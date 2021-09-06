package org.carrat.web.builder

import org.carrat.context.Context
import org.carrat.web.builder.fragment.Fragment
import org.carrat.web.builder.fragment.TagFragment
import org.carrat.web.builder.html.*
import org.carrat.web.webapi.Element

internal class DefaultTagBuilder<T : Tag<T, E>, E : Element>(
    context: Context,
    tagType: TagType<T, E>,
    private val block: TagBlock<T, E>
) : AbstractListBuilder<TagFragment<T, E>, T>(context), TagBuilder<T, E> {
    val attachList = mutableListOf<E.() -> Unit>()

    override fun executeBlock() = block()

    private val handler = DefaultBuilderTagHandler<E>()

    override val tag: T = tagType.createTag(handler)

    override fun doBuild(
        children: Array<Fragment>,
        mountList: Array<() -> Unit>,
        unmountList: Array<() -> Unit>
    ): TagFragment<T, E> {
        val fragment = TagFragment(tag, children, attachList.toTypedArray(), mountList, unmountList, handler.attributes)
        handler.fragment = fragment
        return fragment
    }

    override fun attach(consumer: E.() -> Unit) {
        attachList += consumer
    }
}