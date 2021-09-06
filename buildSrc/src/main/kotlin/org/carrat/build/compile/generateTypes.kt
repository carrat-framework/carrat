package org.carrat.build.compile

import org.carrat.build.codegen.*
import org.carrat.build.humanize
import org.carrat.build.model.Model
import org.carrat.build.model.TagTypeDefinition

fun SourceSet.generateTypes(`package`: String, model: Model) {
    model.types.forEach { generateType(`package`, it.key, it.value) }
}

fun SourceSet.generateType(`package`: String, name: String, type: TagTypeDefinition) {
    val className = name.humanize().capitalize()
    val attributes = type.attributes
    val extraTagInterfaces = type.attributeGroups.map { "${it.capitalize()}<E>" }
    val extraConsumerInterfaces = type.tagGroupNames.map { "${it.toTypeName()}Consumer" } +
            type.elements.map { "${it.toTypeName()}Consumer" }
    file(`package`, "$className.kt") {
        `package`(`package`)
        appendLine()
        import("$webapiPackage.Element")
        appendLine()
        `class`(
            setOf(Modifier.PUBLIC, Modifier.ABSTRACT), "$className<out T : Tag<T, E>, E : Element>",
            listOf(),
            listOf(
                "Tag<T, E>()"
            ) + extraTagInterfaces + extraConsumerInterfaces
        ) {
            generateAttributeTypes(attributes)
        }
        generateAttributes(className, attributes)
    }
}
