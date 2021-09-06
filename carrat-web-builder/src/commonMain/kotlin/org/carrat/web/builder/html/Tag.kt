package org.carrat.web.builder.html

import org.carrat.web.webapi.Element

public abstract class Tag<out T : Tag<T, E>, E : Element>: TagHandler<E> {
    public abstract val tagType : TagType<T, E>
}