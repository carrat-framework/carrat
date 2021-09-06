package org.carrat.build.model

import org.carrat.build.humanize

class TagTypeReference(
    val name : String
) : TagType {
    val className = name.humanize().capitalize()
}