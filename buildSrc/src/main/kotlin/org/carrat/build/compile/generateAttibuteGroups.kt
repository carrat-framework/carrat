package org.carrat.build.compile

import org.carrat.build.codegen.*
import org.carrat.build.model.AttributeDeclaration
import org.carrat.build.model.AttributeGroup
import org.carrat.build.model.Model

fun SourceSet.generateAttributeGroups(`package`: String, model: Model) {
    model.attributeGroups.values.forEach { generateAttributeGroup(`package`, it) }
}

fun SourceSet.generateAttributeGroup(`package`: String, attributeGroup: AttributeGroup) {
    val className = attributeGroup.typeName
    val extraTagInterfaces: List<String> = attributeGroup.parentGroups.map { "${it.capitalize()}<E>" }
    val attributes: List<AttributeDeclaration> = attributeGroup.attributes
    file(`package`, "$className.kt") {
        `package`(`package`)
        appendLine()
        import("$webapiPackage.Element")
        appendLine()
        `interface`(
            setOf(Modifier.PUBLIC), "${className}<out E : Element>",
            listOf(
                "TagHandler<E>"
            ) + extraTagInterfaces
        ) {
            generateAttributeTypes(attributes)
        }
        appendIndentedLine()
        generateAttributes("$className<*>", attributes)
    }
}

