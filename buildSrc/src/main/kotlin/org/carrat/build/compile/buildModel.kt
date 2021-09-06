package org.carrat.build.compile

import com.sun.xml.xsom.*
import org.carrat.build.model.*
import org.carrat.build.humanize
import kotlin.reflect.KClass

fun DeclarationsBuildingContext.buildModel(
    schema: XSSchema,
    tagElementsMatches: TagElementsMatches,
    elementClasses : Collection<KClass<*>>
): Model {
    val tags = schema.elementDecls.values.map { buildTagDeclaration(it, tagElementsMatches) }.associateBy { it.name }
    val types = schema.complexTypes.mapValues { buildTagTypeDefinition(it.key, it.value) }
    val tagGroups = schema.modelGroupDecls.mapValues { buildTagGroup(it.value) }
    val attributeGroups = schema.attGroupDecls.values.map { buildAttributeGroup(it) }.associateBy { it.name }
    val attributeTypes = schema.simpleTypes.mapValues { buildAttributeType(null, null, it.key.toTypeName(), it.value) }
    return Model(
        tags,
        types,
        tagGroups,
        attributeGroups,
        attributeTypes,
        elementClasses
    )
}

fun DeclarationsBuildingContext.buildTagDeclaration(declaration: XSElementDecl, tagElementsMatches : TagElementsMatches): TagDeclaration {
    val complex = declaration.type.asComplexType()
    return if (complex != null) {
        val name = declaration.name
        val typeName = complex.name
        val type = if (typeName != null) {
            TagTypeReference(typeName)
        } else {
            buildTagTypeDefinition(declaration.name, complex)
        }

        TagDeclaration(name, type, docInfos[name]?.empty ?: false, tagElementsMatches.tagElement[name]?:"HTMLElement")
    } else {
        throw UnsupportedOperationException()
    }
}

fun buildTagTypeDefinition(className: String, complex: XSComplexType): TagTypeDefinition {
    val attributeGroups = complex.attGroups.map { it.name }.toList()

    val attributes = complex.declaredAttributeUses.map {
        buildAttributeDeclaration(className, it.decl)
    }

    val elements = mutableListOf<String>()
    val groups = mutableListOf<String>()
    val contentTerm = complex.contentType?.asParticle()?.term
    if (contentTerm != null) {
        collectElements(contentTerm, elements, groups)
    }
    return TagTypeDefinition(elements, attributeGroups, attributes, groups.sorted().toList())
}

fun buildTagGroup(group: XSModelGroupDecl): TagGroup {
    val elements = mutableListOf<String>()
    val groups = mutableListOf<String>()
    collectElements(group.modelGroup, elements, groups)
    return TagGroup(group.name, elements)
}

fun buildAttributeGroup(group: XSAttGroupDecl): AttributeGroup {
    return AttributeGroup(
        group.name,
        group.declaredAttributeUses.map { buildAttributeDeclaration(group.name, it.decl) },
        group.attGroups.map { it.name })
}

fun buildAttributeDeclaration(parent: String, attributeDeclaration: XSAttributeDecl): AttributeDeclaration {
    val name = attributeDeclaration.name
    val type = attributeDeclaration.type

    val parentTypeName = parent.toTypeName()
    val enumTypeNameBase = name.toTypeName()
    val enumTypeName = if (enumTypeNameBase != "Type") {
        enumTypeNameBase
    } else {
        parentTypeName + enumTypeNameBase
    }
    return if (type.name != null && type.name !in xsdToType.keys) {
        AttributeDeclaration(name, AttributeType.Reference(type.name))
    } else {
        AttributeDeclaration(name, buildAttributeType(name, parentTypeName, enumTypeName, type))
    }
}

fun buildAttributeType(attributeName : String?, parentTypeName: String?, enumTypeName : String, type: XSSimpleType): AttributeType {
    if (type.isUnion) {
        val enumEntries = type.asUnion()
            .filter { it.isRestriction }
            .map { it.asRestriction() }
            .flatMap { it.declaredFacets ?: emptyList() }
            .filter { it.name == "enumeration" }
            .map { it.value.value }

        return AttributeType.Enum(parentTypeName, enumEntries.toSet(), enumTypeName, true)
    } else if (type.name in xsdToType.keys) {
        return xsdToType[type.name]!!
    } else if (type.isRestriction) {
        val restriction = type.asRestriction()
        val enumEntries = restriction.declaredFacets
            .filter { it.name == "enumeration" }
            .map { it.value.value }

        return if (enumEntries.size == 1 && enumEntries.single() == attributeName) {
            // probably ticker
            AttributeType.Ticker
        } else if (enumEntries.size == 2 && enumEntries.sorted() == listOf("off", "on")) {
            AttributeType.OnOff
        } else if (enumEntries.isEmpty()) {
            AttributeType.StringType
        } else {
            AttributeType.Enum(
                parentTypeName, enumEntries.toSet(),
                enumTypeName,
                false
            )
        }
    } else {
        return AttributeType.StringType
    }
}

fun String.toTypeName() = humanize().capitalize()

private val xsdToType = mapOf(
    "boolean" to AttributeType.BooleanType,
    "string" to AttributeType.StringType,
    "integer" to AttributeType.IntegerType,
    "float" to AttributeType.FloatType,
    "positiveInteger" to AttributeType.PositiveIntegerType,
    "anyURI" to AttributeType.URI, // TODO links
    "anySimpleType" to AttributeType.StringType,
    "dateTime" to AttributeType.StringType,
)

private fun collectElements(term: XSTerm, elements: MutableCollection<String>, groups: MutableCollection<String>) {
    if (term.isElementDecl) {
        elements.add(term.asElementDecl().name)
    } else if (term.isModelGroupDecl) {
        groups.add(term.asModelGroupDecl().name)
    } else if (term.isModelGroup) {
        term.asModelGroup().forEach {
            collectElements(it.term, elements, groups)
        }
    }
}