package org.carrat.web.builder.fragment

import org.carrat.web.webapi.Comment

internal enum class Marker(val string: String) {
    START("{"),
    END("}"),
    SEPARATOR(","),;
}

internal fun asMarker(comment: Comment): Marker? {
    return Marker.values().find { it.string == comment.textContent }
}
