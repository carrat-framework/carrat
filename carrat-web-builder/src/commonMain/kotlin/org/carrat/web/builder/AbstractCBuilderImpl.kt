package org.carrat.web.builder

import kotlinx.html.Entities
import kotlinx.html.Tag
import kotlinx.html.Unsafe
import org.carrat.web.fragments.Fragment
import org.carrat.web.fragments.TextFragment
import org.carrat.context.Context
import org.carrat.web.webapi.Element

internal sealed class AbstractCBuilderImpl(
    context: Context
) : AbstractCFragmentBuilder(context), CBuilder {
    private val path = arrayListOf<AbstractCFragmentBuilder>()
    protected val children: MutableList<Fragment> = arrayListOf()

    override fun onTagStart(tag: Tag) {
        val last = path.lastOrNull()
        if (last is AbstractCBuilderImpl || last == null) {
            val element = childTagBuilder(tag)
            path.add(element)
        } else {
            last.onTagStart(tag)
        }
    }

    override fun onTagEnd(tag: Tag) {
        val last = path.last()
        if (last is CAnyTagBuilder<*> && last.tag == tag) {
            val lastParent = path.getOrNull(path.size - 2) ?: this
            (lastParent as AbstractCBuilderImpl).children += last.finalize()
            path.removeLast()
        } else {
            last.onTagEnd(tag)
        }
    }

    private fun childTagBuilder(tag: Tag): AbstractCFragmentBuilder =
        CTagBuilderImpl(
            { _: CTagBuilder<Tag> -> tag },
            {},
            context
        )

    override fun onTagAttributeChange(tag: Tag, attribute: String, value: String?) {
        val last = path.lastOrNull()
        if (last == null) {
            doOnTagAttributeChange(tag, attribute, value)
        } else {
            last.onTagAttributeChange(tag, attribute, value)
        }

    }

    override fun onTagContent(content: CharSequence) {
        val last = path.lastOrNull()
        if (last == null) {
            children += TextFragment(content.toString())
        } else {
            last.onTagContent(content)
        }
    }

    override fun onTagContentEntity(entity: Entities) {
        val last = path.lastOrNull()
        if (last == null) {
            children += TextFragment(entity.toString())
        } else {
            last.onTagContentEntity(entity)
        }
    }

    override fun append(fragment: Fragment) {
        when (val last = path.lastOrNull()) {
            null -> {
                children += fragment
            }
            is CBuilder -> {
                last.append(fragment)
            }
            else -> {
                throw UnsupportedOperationException("Fragments cannot be embedded in unsafe context.")
            }
        }
    }

    override fun onTagContentUnsafe(block: Unsafe.() -> Unit) {
        val last = path.lastOrNull()
        if (last == null) {
            throw UnsupportedOperationException("Not allowed outside unsafe tag.")
        } else {
            last.onTagContentUnsafe(block)
        }
    }

    override fun onMount(callback: () -> Unit) {
        val last = path.lastOrNull()
        if (last == null) {
            onMount += callback
        } else {
            last.onMount(callback)
        }
    }

    override fun onUnmount(callback: () -> Unit) {
        val last = path.lastOrNull()
        if (last == null) {
            onUnmount += callback
        } else {
            last.onUnmount(callback)
        }
    }

    final override fun <E : Element> attach(consumer: (E) -> Unit) {
        val last = path.lastOrNull()
        if (last == null) {
            doAttach { consumer(it as E) }
        } else {
            last.attach(consumer)
        }
    }

    protected abstract fun doAttach(consumer: (Element) -> Unit)
    protected abstract fun doOnTagAttributeChange(tag: Tag, attribute: String, value: String?)
}
