package org.carrat.build.model

import org.carrat.build.humanize

data class TagDeclaration(
    val name: String,
    val type : TagType,
    val empty : Boolean,
    val elementType : String,
) {
    val memberName: String = name.humanize()
    val className: String = memberName.capitalize()
}

