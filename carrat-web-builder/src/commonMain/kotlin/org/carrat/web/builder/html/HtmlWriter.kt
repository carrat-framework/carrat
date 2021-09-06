package org.carrat.web.builder.html

import kotlin.collections.Map

public interface HtmlWriter {
    public fun writeTag(tagType: TagType<*, *>, attributes: Map<String, String>, htmlBlock: HtmlBlock? = null)
    public fun writeText(content : Appendable.()->Unit)
    public fun writeText(content : CharSequence)
    public fun writeUnsafe(unsafeContent : Appendable.()->Unit)
    public fun writeUnsafe(unsafeContent : CharSequence)
    public fun writeComment(content : Appendable.()->Unit)
    public fun writeComment(content : CharSequence)
}
