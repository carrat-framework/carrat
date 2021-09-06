package org.carrat.web.builder.html

import org.carrat.web.webapi.Element

public abstract class TagType<out T : Tag<T, E>, E : Element>(
    public val namespace : String?,
    public val name : String,
    public val empty : Boolean
) {
    public constructor(name : String, empty : Boolean) : this(null, name, empty)

    public abstract fun createTag(handler: TagHandler<E>) : T
}