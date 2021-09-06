package org.carrat.build.model

class TagTypeDefinition(
    val elements: List<String>,
    val attributeGroups: List<String>,
    val attributes: List<AttributeDeclaration>,
    val tagGroupNames: List<String>
) : TagType