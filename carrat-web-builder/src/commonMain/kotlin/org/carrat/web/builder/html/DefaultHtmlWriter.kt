package org.carrat.web.builder.html

import kotlin.collections.Map

public class DefaultHtmlWriter(private val out : Appendable) : HtmlWriter {
    override fun writeTag(tagType: TagType<*, *>, attributes: Map<String, String>, htmlBlock: HtmlBlock?) {
        out.append("<")
        out.append(tagType.name)

        if (tagType.namespace != null) {
            out.append(" xmlns=\"")
            out.append(tagType.namespace)
            out.append("\"")
        }

        if (attributes.isNotEmpty()) {
            attributes.entries.forEachIndexed { _, e ->
                if (!e.key.isValidXmlAttributeName()) {
                    throw IllegalArgumentException("Tag ${tagType.name} has invalid attribute name ${e.key}")
                }

                out.append(' ')
                out.append(e.key)
                out.append("=\"")
                out.escapeAppend(e.value)
                out.append('\"')
            }
        }

        if (tagType.empty) {
            out.append("/>")
        } else {
            out.append(">")
            htmlBlock?.invoke(this)
            out.append("</")
            out.append(tagType.name)
            out.append(">")
        }
    }

    override fun writeText(content: Appendable.() -> Unit) {
        val sb = StringBuilder()
        sb.content()
        writeText(sb.toString())
    }

    override fun writeText(content: CharSequence) {
        out.escapeAppend(content)
    }

    override fun writeUnsafe(unsafeContent: Appendable.() -> Unit) {
        val sb = StringBuilder()
        sb.unsafeContent()
        writeUnsafe(sb.toString())
    }

    override fun writeUnsafe(unsafeContent: CharSequence) {
        out.append(unsafeContent)
    }

    override fun writeComment(content: Appendable.() -> Unit) {
        val sb = StringBuilder()
        sb.content()
        writeComment(sb.toString())
    }

    override fun writeComment(content: CharSequence) {
        out.append("<!--")
        out.escapeComment(content)
        out.append("-->")
    }
}

private val escapeMap = mapOf(
    '<' to "&lt;",
    '>' to "&gt;",
    '&' to "&amp;",
    '\"' to "&quot;"
).let { mappings ->
    val maxCode = mappings.keys.map { it.code }.maxOrNull() ?: -1

    Array(maxCode + 1) { mappings[it.toChar()] }
}

private val letterRangeLowerCase = 'a'..'z'
private val letterRangeUpperCase = 'A'..'Z'
private val digitRange = '0'..'9'

private fun Char._isLetter() = this in letterRangeLowerCase || this in letterRangeUpperCase
private fun Char._isDigit() = this in digitRange

private fun String.isValidXmlAttributeName() =
    !startsWithXml()
            && this.isNotEmpty()
            && (this[0]._isLetter() || this[0] == '_')
            && this.all { it._isLetter() || it._isDigit() || it in "._:-" }


private fun String.startsWithXml() = length >= 3
        && (this[0].let { it == 'x' || it == 'X' })
        && (this[1].let { it == 'm' || it == 'M' })
        && (this[2].let { it == 'l' || it == 'L' })


private fun Appendable.escapeAppend(s: CharSequence) {
    var lastIndex = 0
    val mappings = escapeMap
    val size = mappings.size

    for (idx in 0..s.length - 1) {
        val ch = s[idx].code
        if (ch < 0 || ch >= size) continue
        val escape = mappings[ch]
        if (escape != null) {
            append(s.substring(lastIndex, idx))
            append(escape)
            lastIndex = idx + 1
        }
    }

    if (lastIndex < s.length) {
        append(s.substring(lastIndex, s.length))
    }
}

private fun Appendable.escapeComment(s: CharSequence) {
    var start = 0
    while (start < s.length) {
        val index = s.indexOf("--")
        if (index == -1) {
            if (start == 0) {
                append(s)
            } else {
                append(s, start, s.length)
            }
            break
        }

        append(s, start, index)
        start += 2
    }
}