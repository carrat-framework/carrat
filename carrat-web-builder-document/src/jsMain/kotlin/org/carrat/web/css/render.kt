package org.carrat.web.css

import kotlinx.html.*
import org.carrat.web.fragments.mount
import org.carrat.web.fragments.render
import org.carrat.web.builder.build
import org.carrat.context.Context
import org.carrat.web.builder.document.DocumentTemplate
import org.carrat.web.webapi.*
import org.w3c.dom.events.Event

public fun Document.render(template: DocumentTemplate, context: Context) {
    val headFragment = context.build(template.head)
    val bodyFragment = context.build(template.body)
    val head = this.head?:insertBefore(createElement("head"), firstChild) as HTMLHeadElement
    val body = this.body?:appendChild(createElement("body")) as HTMLBodyElement
//    JSDOMBuilder(head).render(headFragment)
//    JSDOMBuilder(body).render(bodyFragment)
    head.mount(headFragment)
    body.mount(bodyFragment)
}


@Suppress("NOTHING_TO_INLINE")
internal inline fun Element.setEvent(name: String, noinline callback: (Event) -> Unit) {
    asDynamic()[name] = callback
}

private class JSDOMBuilder(val node: Element) : TagConsumer<Element> {
    val document: Document = node.ownerDocumentExt
    private val path = arrayListOf(node)
    private var lastLeft: Element? = null

    override fun onTagStart(tag: Tag) {
        val element: Element = when {
            tag.namespace != null -> document.createElementNS(tag.namespace!!, tag.tagName)
            else -> document.createElement(tag.tagName)
        }

        tag.attributesEntries.forEach {
            element.setAttribute(it.key, it.value)
        }

        if (path.isNotEmpty()) {
            path.last().appendChild(element)
        }

        path.add(element)
    }

    override fun onTagAttributeChange(tag: Tag, attribute: String, value: String?) {
        when {
            path.isEmpty() -> throw IllegalStateException("No current tag")
            path.last().tagName.toLowerCase() != tag.tagName.toLowerCase() -> throw IllegalStateException("Wrong current tag")
            else -> path.last().let { node ->
                if (value == null) {
                    node.removeAttribute(attribute)
                } else {
                    node.setAttribute(attribute, value)
                }
            }
        }
    }

    override fun onTagEvent(tag: Tag, event: String, value: (Event) -> Unit) {
        when {
            path.isEmpty() -> throw IllegalStateException("No current tag")
            path.last().tagName.toLowerCase() != tag.tagName.toLowerCase() -> throw IllegalStateException("Wrong current tag")
            else -> path.last().setEvent(event, value)
        }
    }

    override fun onTagEnd(tag: Tag) {
        if (path.isEmpty() || path.last().tagName.toLowerCase() != tag.tagName.toLowerCase()) {
            throw IllegalStateException("We haven't entered tag ${tag.tagName} but trying to leave")
        }

        lastLeft = path.removeAt(path.lastIndex)
    }

    override fun onTagContent(content: CharSequence) {
        if (path.isEmpty()) {
            throw IllegalStateException("No current DOM node")
        }

        path.last().appendChild(document.createTextNode(content.toString()))
    }

    override fun onTagContentEntity(entity: Entities) {
        if (path.isEmpty()) {
            throw IllegalStateException("No current DOM node")
        }

        // stupid hack as browsers doesn't support createEntityReference
        val s = document.createElement("span") as HTMLElement
        s.innerHTML = entity.text
        path.last().appendChild(s.childNodes.asList().first { it.nodeType == Node.TEXT_NODE })

        // other solution would be
//        pathLast().innerHTML += entity.text
    }

    override fun onTagContentUnsafe(block: Unsafe.() -> Unit) {
        with(DefaultUnsafe()) {
            block()

            path.last().innerHTML += toString()
        }
    }


    override fun onTagComment(content: CharSequence) {
        if (path.isEmpty()) {
            throw IllegalStateException("No current DOM node")
        }

        path.last().appendChild(document.createComment(content.toString()))
    }

    override fun finalize(): Element = node
}

private val Node.ownerDocumentExt: Document
    get() = when {
        this is Document -> this
        else -> ownerDocument ?: throw IllegalStateException("Node has no ownerDocument")
    }
