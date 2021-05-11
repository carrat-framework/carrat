package org.carrat.web.fragments

import kotlinx.html.TagConsumer
import org.carrat.web.webapi.*

public class DynamicListFragment(
    initialValue: List<Fragment>?
) : Fragment() {
    public val children: MutableList<Fragment> = object : AbstractMutableList<Fragment>() {
        override val size: Int
            get() = lastList?.size ?: 0

        override fun get(index: Int): Fragment = (lastList ?: manipulationOnUninitialized())[index].fragment

        override fun add(index: Int, element: Fragment) = mountContent(index, element)

        override fun removeAt(index: Int): Fragment = unmountContent(index)

        override fun set(index: Int, element: Fragment): Fragment {
            val removedFragment = get(index)
            if (removedFragment != element) {
                replaceContent(index, element)
            }
            return removedFragment
        }
    }

    override val attached: Boolean
        get() = ::startMarker.isInitialized

    override fun TagConsumer<*>.render() {
        onTagComment(Marker.START.string)
        for (child in children) {
            onTagComment(Marker.SEPARATOR.string)
            with(child) { render() }
        }
        onTagComment(Marker.END.string)
    }

    private lateinit var startMarker: Comment
    private lateinit var endMarker: Comment
    private var lastList = initialValue?.map { ElementWrapper(it, null) }?.toMutableList()
    private var mounted: Boolean = false
    private var domMounted: Boolean = false
    private var deferredHydration: Boolean = false

    override fun attach() {
        startMarker = document.createComment(Marker.START.string)
        val lastList = lastList
        if (lastList != null) {
            for (child in lastList) {
                child.fragment.attach()
            }
        }
        endMarker = document.createComment(Marker.END.string)
    }

    override fun mount() {
        children.forEach { it.mount() }
        mounted = true
    }

    override fun unmount() {
        val lastList = lastList
        if (lastList != null) {
            lastList.forEach { it.fragment.unmount() }
        }
        mounted = false
    }

    override fun mountDom(parent: Element, after: Node?): Node {
        parent.insertAfter(startMarker, after)
        var lastNode: Node? = startMarker
        val lastList = lastList
        if (lastList != null) {
            for (child in lastList) {
                val separator = child.giveStartSeparator()
                parent.insertAfter(separator, lastNode)
                val fragment = child.fragment
                if(!fragment.attached) {
                    fragment.attach()
                }
                lastNode = fragment.mountDom(parent, separator)
            }
        }
        parent.insertAfter(endMarker, lastNode)
        domMounted = true
        return endMarker
    }

    override fun unmountDom() {
        var next = startMarker.nextSibling!!
        startMarker.removeFromParent()
        val lastList = lastList
        if (lastList != null) {
            for (child in lastList) {
                child.startSeparator!!.removeFromParent()
                child.fragment.unmountDom()
            }
        } else if (deferredHydration) {
            deferredHydration = false
            while (next != endMarker) {
                val current = next
                next = current.nextSibling!!
                current.removeFromParent()
            }
        }
        endMarker.removeFromParent()
        domMounted = false
    }

    public fun initialize(initialValue: List<Fragment>) {
        if (lastList == null) {
            if (deferredHydration) {
                lastList = initialValue.map { ElementWrapper(it, null) }.toMutableList()
                val nodeIterator = ElementIterator(startMarker.parentElement!!, startMarker, endMarker).asSequence()
                    .mapNotNull { it.asToken() }.iterator()
                for (child in lastList!!) {
                    child.startSeparator = nodeIterator.requireMarker(Marker.SEPARATOR)
                    child.fragment.hydrate(nodeIterator)
                }
                deferredHydration = false
            } else {
                lastList = mutableListOf()
                children.addAll(initialValue)
            }
        } else {
            initializationOnInitialized()
        }
    }

    public fun replace(initialValue: List<Fragment>) {
        val previousLastList = lastList
        if(mounted) {
            previousLastList?.forEach {
                it.fragment.unmount()
            }
        }
        if(domMounted) {
            previousLastList?.forEach {
                it.fragment.unmountDom()
            }
        }
        deferredHydration = false
        lastList = mutableListOf()
        children.addAll(initialValue)
    }

    override fun hydrate(nodeIterator: Iterator<NodeToken>) {
        startMarker = nodeIterator.requireMarker(Marker.START)
        val lastList = lastList
        if (lastList != null) {
            for (child in lastList) {
                child.startSeparator = nodeIterator.requireMarker(Marker.SEPARATOR)
                child.fragment.hydrate(nodeIterator)
            }
        } else {
            deferredHydration = true
            var neasted = 0
            while (true) {
                val token = nodeIterator.next()
                if (token is MarkerToken) {
                    when (token.marker) {
                        Marker.START -> {
                            neasted++
                        }
                        Marker.END -> {
                            if (neasted == 0) {
                                break
                            } else {
                                neasted--
                            }
                        }
                        Marker.SEPARATOR -> {
                        }
                    }
                }
            }
        }
        domMounted = true
        mounted = true
        endMarker = nodeIterator.requireMarker(Marker.END)
    }

    private inline fun <T> manipulate(manipulation: MutableList<ElementWrapper>.() -> T): T {
        val lastList = lastList
        return if (lastList != null) {
            manipulation(lastList)
        } else {
            manipulationOnUninitialized()
        }
    }

    private fun mountContent(index: Int, fragment: Fragment) = manipulate {
        val elementWrapper = ElementWrapper(fragment, document.createComment(Marker.SEPARATOR.string))
        if (domMounted) {
            val before = getOrNull(index)?.startSeparator ?: endMarker
            val startSeparator = elementWrapper.startSeparator!!
            val parent = before.parentElement!!
            parent.insertBefore(startSeparator, before)
            if(!fragment.attached) {
                fragment.attach()
            }
            fragment.mountDom(parent, startSeparator)
            if (mounted) {
                fragment.mount()
            }
        }
        add(index, elementWrapper)
    }

    private fun unmountContent(index: Int): Fragment = manipulate {
        val (fragment, startSeparator) = removeAt(index)
        if (domMounted) {
            startSeparator!!.removeFromParent()
            if (mounted) {
                fragment.unmount()
            }
            fragment.unmountDom()
        }
        return fragment
    }

    private fun replaceContent(index: Int, fragment: Fragment) = manipulate {
        val elementWrapper = get(index)
        if (domMounted) {
            val oldFragment = elementWrapper.fragment
            if (mounted) {
                oldFragment.unmount()
            }
            oldFragment.unmountDom()
            val startSeparator = elementWrapper.startSeparator!!
            if(!fragment.attached) {
                fragment.attach()
            }
            fragment.mountDom(startSeparator.parentElement!!, startSeparator)
            if (mounted) {
                fragment.mount()
            }
        }
        elementWrapper.fragment = fragment
    }

    private data class ElementWrapper(
        var fragment: Fragment,
        var startSeparator: Comment? = null
    ) {
        fun giveStartSeparator(): Comment {
            if (startSeparator == null) {
                startSeparator = document.createComment(Marker.SEPARATOR.string)
            }
            return startSeparator!!
        }
    }
}

private fun manipulationOnUninitialized(): Nothing {
    throw IllegalStateException("Dynamic list fragment must be initialized, before manipulating it content.")
}

private fun initializationOnInitialized(): Nothing {
    throw IllegalStateException("Dynamic list fragment must not be initialized multiple times.")
}
