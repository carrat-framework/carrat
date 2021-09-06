package org.carrat.build.model

import org.carrat.build.compile.toTypeName

sealed class AttributeType(private val alias: String?, val typeName: String) {
    object StringType : AttributeType("String", "String")
    object BooleanType : AttributeType("Boolean", "Boolean")
    object IntegerType : AttributeType("Integer", "Int")
    object FloatType : AttributeType("Float", "Float")
    object PositiveIntegerType : AttributeType("PositiveInteger", "UInt")
    object OnOff : AttributeType("OnOff", "Boolean")
    object Ticker : AttributeType("Ticker", "Boolean")
    class Reference(val name: String) : AttributeType(null, name.toTypeName()) {
        override val fullAttributeTypeValName: String
            get() = "${typeName}.${attributeTypeValName}"
    }

    class Enum(
        override val parent: String?,
        val values: Set<String>,
        typeName: String,
        open: Boolean
    ) : AttributeType(null, typeName) {
        override val fullAttributeTypeValName: String
            get() = if (parent != null) {
                "${parent}.${typeName}.${attributeTypeValName}"
            } else {
                "${typeName}.${attributeTypeValName}"
            }
    }

    open val parent: String?
        get() = null

    open val fullAttributeTypeValName: String
        get() = attributeTypeValName

    val attributeTypeValName: String
        get() = if(alias != null) {
            "${alias.decapitalize()}AttributeType"
        } else {
            "attributeType"
        }

    companion object {
        val URI = StringType
    }
}