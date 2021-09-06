package org.carrat.build.model

import org.carrat.build.humanize

data class TagGroup(
        val name : String,
        val elements : List<String>) {

    val memberName: String = name.humanize()
    val typeName: String = memberName.capitalize()
    val consumerTypeName : String = "${typeName}Consumer"
}