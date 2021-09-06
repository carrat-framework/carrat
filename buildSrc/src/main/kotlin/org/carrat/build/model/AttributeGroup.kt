package org.carrat.build.model

import org.carrat.build.compile.toTypeName

data class AttributeGroup(val name : String, val attributes : List<AttributeDeclaration>, val parentGroups : List<String>) {
    val typeName = name.toTypeName()
}