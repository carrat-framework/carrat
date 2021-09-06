package org.carrat.web.css

import org.carrat.web.builder.build
import org.carrat.context.Context
import org.carrat.web.builder.document.DocumentTemplate
import org.carrat.web.builder.fragment.mount
import org.carrat.web.builder.html.HtmlBlock
import org.carrat.web.builder.html.HtmlWriter
import org.carrat.web.builder.html.TagType
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

private class JSDOMBuilder(val node: Element, val document: Document = node.ownerDocumentExt) : HtmlWriter {
    override fun writeTag(tagType: TagType<*, *>, attributes: Map<String, String>, htmlBlock: HtmlBlock?) {
        val namespace = tagType.namespace
        val tagName = tagType.name
        val element: Element = when {
            namespace != null -> document.createElementNS(namespace, tagName)
            else -> document.createElement(tagName)
        }

        attributes.forEach {
            element.setAttribute(it.key, it.value)
        }
        if (htmlBlock != null) {
            JSDOMBuilder(element, document).htmlBlock()
        }
        node.append(element)
    }

    override fun writeText(content: Appendable.() -> Unit) {
        val sb = StringBuilder()
        sb.content()
        writeText(sb.toString())
    }

    override fun writeText(content: CharSequence) {
        node.appendChild(document.createTextNode(content.toString()))
    }

    override fun writeUnsafe(unsafeContent: Appendable.() -> Unit) {
        val sb = StringBuilder()
        sb.unsafeContent()
        writeUnsafe(sb.toString())
    }

    override fun writeUnsafe(unsafeContent: CharSequence) {
        node.innerHTML += unsafeContent
    }

    override fun writeComment(content: Appendable.() -> Unit) {
        val sb = StringBuilder()
        sb.content()
        writeComment(sb.toString())
    }

    override fun writeComment(content: CharSequence) {
        node.appendChild(document.createComment(content.toString()))
    }
}

private val Node.ownerDocumentExt: Document
    get() = when {
        this is Document -> this
        else -> ownerDocument ?: throw IllegalStateException("Node has no ownerDocument")
    }
