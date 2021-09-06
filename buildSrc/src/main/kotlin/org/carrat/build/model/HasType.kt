package org.carrat.build.model

interface HasType {
    val type : AttributeType
}

//val HasType.typeName : String
//    get() = if (type == AttributeType.ENUM) enumTypeName else type.typeName