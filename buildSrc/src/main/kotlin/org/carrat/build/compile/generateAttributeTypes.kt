package org.carrat.build.compile

import org.carrat.build.codegen.SourceSet
import org.carrat.build.codegen.`package`
import org.carrat.build.codegen.file
import org.carrat.build.model.AttributeType
import org.carrat.build.model.Model

fun SourceSet.generateAttributeTypes(`package`: String, model: Model) {
    model.attributeTypes.forEach { generateAttributeType(`package`, it.value) }
}

fun SourceSet.generateAttributeType(`package`: String, type: AttributeType) {
    val className = type.typeName
    file(`package`, "$className.kt") {
        `package`(`package`)
        appendLine()
        generateAttributeType(type as AttributeType.Enum)
    }
}
