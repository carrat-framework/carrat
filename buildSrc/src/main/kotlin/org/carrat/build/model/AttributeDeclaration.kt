package org.carrat.build.model

import org.carrat.build.humanize

data class AttributeDeclaration(
    val name: String,
    override val type: AttributeType,
    val required: Boolean = false
) : HasType {
    val fieldName: String = name.humanize()
}