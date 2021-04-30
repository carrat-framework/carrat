package org.carrat.web.builder

import kotlinx.html.Tag
import org.carrat.web.webapi.Element

internal interface AbstractCAnyTagBuilder<T : Tag> : CAnyTagBuilder<T> {
    val element : Element?

    fun doOnTagAttributeChange(tag: Tag, attribute: String, value: String?) {
        if(tag == this.tag) {
            val element = element
            if(element != null) {
                if (value != null) {
                    element.setAttribute(attribute, value)
                } else {
                    element.removeAttribute(attribute)
                }
            }
        } else {
            throw UnsupportedOperationException("Tag attribute can't be changed this way.")//TODO
        }
    }
}
