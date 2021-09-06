package org.carrat.build.compile

import org.carrat.build.codegen.*
import org.carrat.build.model.*

fun SourceSet.generateTags(`package`: String, model: Model) {
    model.tags.values.forEach { generateTag(`package`, it) }
}

fun SourceSet.generateTag(`package`: String, tag: TagDeclaration) {
    val className = tag.className
    val type = tag.type
    val parentClass: String
    val extraTagInterfaces: List<String>
    val extraConsumerInterfaces: List<String>
    val attributes: List<AttributeDeclaration>
    when (type) {
        is TagTypeDefinition -> {
            parentClass = "Tag<$className, ${tag.elementType}>()"
            extraTagInterfaces = type.attributeGroups.map { "${it.capitalize()}<${tag.elementType}>" }
            extraConsumerInterfaces = type.tagGroupNames.map { "${it.toTypeName()}Consumer" } +
                    type.elements.map { "${it.toTypeName()}Consumer" }
            attributes = type.attributes
        }
        is TagTypeReference -> {
            val typeClassName = type.className
            parentClass = "$typeClassName<$className, ${tag.elementType}>()"
            extraTagInterfaces = emptyList()
            extraConsumerInterfaces = emptyList()
            attributes = emptyList()
        }
    }
    file(`package`, "$className.kt") {
        `package`(`package`)
        appendLine()
        import("$webapiPackage.${tag.elementType}")
        appendLine()
        `class`(
            setOf(Modifier.PUBLIC), className,
            listOf("private val handler : TagHandler<${tag.elementType}>"),
            listOf(
                parentClass,
                "TagHandler<${tag.elementType}> by handler"
            ) + extraTagInterfaces + extraConsumerInterfaces
        ) {
            companionObject(
                setOf(Modifier.PUBLIC),
                listOf("TagType<$className, ${tag.elementType}>(\"${tag.name}\", ${tag.empty})")
            ) {
                `fun`(
                    setOf(Modifier.OVERRIDE), null, "createTag",
                    listOf("handler: TagHandler<${tag.elementType}>"), className, "$className(handler)"
                )
            }
            appendIndentedLine()
            `val`(setOf(Modifier.OVERRIDE), "tagType", "$className.Companion", className)
            generateAttributeTypes(attributes)
        }
        appendIndentedLine()
        `interface`(
            setOf(Modifier.PUBLIC), "${className}Consumer",
            listOf()
        )
        appendIndentedLine()
        `fun`(
            setOf(Modifier.PUBLIC), "TagConsumer<${className}Consumer>", tag.memberName,
            listOf("block : TagBlock<$className, ${tag.elementType}>"),
            className,
            "tag($className, block)"
        )
        appendIndentedLine()
        generateAttributes(className, attributes)
    }
}

