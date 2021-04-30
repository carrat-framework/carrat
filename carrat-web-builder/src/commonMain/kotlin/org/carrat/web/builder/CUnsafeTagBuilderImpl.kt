package org.carrat.web.builder

import kotlinx.css.CSSBuilder
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.carrat.web.fragments.UnsafeFragment
import org.carrat.context.Context
import org.carrat.experimental.ExperimentalMultipleReceivers
import org.carrat.web.webapi.Element

@OptIn(ExperimentalMultipleReceivers::class)
internal class CUnsafeTagBuilderImpl<T : Tag>(
    tagConstructor: TagConstructor<T>,
    consumer: CUnsafeTagBlock<T>,
    context: Context
) : AbstractCFragmentBuilder(context), AbstractCAnyTagBuilder<T>, CUnsafeTagBuilder<T> {
    override val tag = tagConstructor(emptyMap(), this)
    override val css = CSSBuilder()
    private val content = StringBuilder()
    override var element: Element? = null
    private val attach = mutableListOf<(Element) -> Unit>({ element = it })
    private val path: MutableList<TagChild> = mutableListOf(TagChild(tag))
    init {
        consumer()
    }

    override fun onTagContentUnsafe(block: Unsafe.() -> Unit) {
        val unsafe = DefaultUnsafe()
        unsafe.block()
        path.last().children += UnsafeChild(unsafe.toString())
    }

    override fun String.unaryPlus() {
        content.append(this)
    }

    override fun <E : Element>attach(consumer: (E) -> Unit) {
        attach += { consumer(it as E) }
    }

    override fun onTagStart(tag: Tag) {
        path += TagChild(tag)
    }

    override fun onTagEnd(tag: Tag) {
        val last = path.removeLast()
        path.last().children += last
    }

    override fun onTagContent(content: CharSequence) {
        path.last().children += TextChild(content)
    }

    override fun onTagContentEntity(entity: Entities) {
        path.last().children += TextChild(entity.text)
    }

    override fun onMount(callback: () -> Unit) {
        if(path.size == 1) {
            onMount += callback
        } else {
            throw IllegalStateException(
                "Cannot attach onMount/onUnmount to tags nested inside unsafe, as they do not have corresponding fragments."
            )
        }
    }

    override fun onUnmount(callback: () -> Unit) {
        if(path.size == 1) {
            onUnmount += callback
        } else {
            throw IllegalStateException(
                "Cannot attach onMount/onUnmount to tags nested inside unsafe, as they do not have corresponding fragments."
            )
        }
    }

    override fun finalize(): UnsafeFragment<T> {
        tag.applyCss(css)
        val content = StringBuilder()
        val tagConsumer = content.appendHTML(false)
        path.first().children.forEach { it.render(tagConsumer) }
        return UnsafeFragment(
            tag,
            content.toString(),
            attach.toTypedArray(),
            onMount.toTypedArray(),
            onUnmount.toTypedArray()
        )
    }

    override fun onTagAttributeChange(tag: Tag, attribute: String, value: String?) {
        doOnTagAttributeChange(tag, attribute, value)
    }
}

private sealed class Child {
    abstract fun render(out: TagConsumer<*>)
}

private class UnsafeChild(val unsafe: String) : Child() {
    override fun render(out: TagConsumer<*>) {
        out.onTagContentUnsafe { +unsafe }
    }
}

private class TextChild(val text: CharSequence) : Child() {
    override fun render(out: TagConsumer<*>) {
        out.onTagContent(text)
    }
}

private class TagChild(val tag: Tag) : Child() {
    val children = mutableListOf<Child>()
    override fun render(out: TagConsumer<*>) {
        out.onTagStart(tag)
        children.forEach { it.render(out) }
        out.onTagEnd(tag)
    }
}
