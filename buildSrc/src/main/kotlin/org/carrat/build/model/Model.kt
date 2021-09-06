package org.carrat.build.model

import kotlin.reflect.KClass

class Model(
    val tags : Map<String, TagDeclaration>,
    val types : Map<String, TagTypeDefinition>,
    val tagGroups : Map<String, TagGroup>,
    val attributeGroups: Map<String, AttributeGroup>,
    val attributeTypes: Map<String, AttributeType>,
    val elementClasses : Collection<KClass<*>>
)