package org.carrat.web.builder.html

import org.carrat.web.webapi.Element

public interface TagHandler<out E : Element> {
    public fun getElement() : E
    public fun getAttribute(attributeName : String) : String?
    public fun setAttribute(attributeName : String, value : String?)
}