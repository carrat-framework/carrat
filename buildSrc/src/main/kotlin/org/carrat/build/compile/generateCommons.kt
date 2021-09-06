package org.carrat.build.compile

import org.carrat.build.codegen.*
import org.carrat.build.humanize
import org.carrat.build.model.AttributeDeclaration
import org.carrat.build.model.AttributeType


val AttributeType.attributeReceiver: String
    get() {
        return if (parent != null) {
            "${parent}.${attributeTypeValName}"
        } else {
            attributeTypeValName
        }
    }

val AttributeType.fullTypeName: String
    get() {
        return if (parent != null) {
            "${parent}.${typeName}"
        } else {
            typeName
        }
    }

fun IndentedAppendable.generateAttributes(
    className: String,
    attributes: List<AttributeDeclaration>
) {
    attributes.forEach {
        appendIndent()
        appendLine()
        `var`(setOf(Modifier.PUBLIC), "$className.${Identifier(it.fieldName)}", "${it.type.fullTypeName}?", null,
            Getter(emptySet(), null,
                ExpressionFunctionBody {
                    append("${it.type.fullAttributeTypeValName}.get(this, \"${it.name}\")")
                }
            ),
            Setter(emptySet(), "newValue", null, Block {
                append("${it.type.fullAttributeTypeValName}.set(this, \"${it.name}\", newValue)")
                appendLine()
            })
        )
    }
}

fun IndentedAppendable.generateAttributeTypes(attributes: List<AttributeDeclaration>) {
    attributes.map { it.type }.filterIsInstance<AttributeType.Enum>().forEach { enum ->
        appendIndentedLine()
        generateAttributeType(enum)
    }
}

fun IndentedAppendable.generateAttributeType(enum: AttributeType.Enum) {
    enum(
        setOf(Modifier.PUBLIC), enum.typeName, listOf(
            "override val string : String"
        ), listOf("AttributeEnum")
    ) {
        enum.values.withIndex().forEach { (k, v) ->
            appendIndent()
            append(Identifier(v.humanize()).toString())
            append("(\"")
            append(v)
            append("\")")
            if (enum.values.size != k + 1) {
                append(",")
            } else {
                append(";")
            }
            appendLine()
        }
        appendIndentedLine()
        companionObject(setOf(Modifier.PUBLIC)) {
            `val`(setOf(Modifier.INTERNAL), "byString", null, "values().associateBy { it.string }")
            appendIndentedLine()
            `fun`(
                setOf(Modifier.PUBLIC),
                null,
                "byString",
                listOf("string : String"),
                "${enum.typeName}?",
                "byString[string]"
            )
            appendIndentedLine()
            `val`(
                setOf(Modifier.INTERNAL),
                "attributeType",
                "Attribute<${enum.typeName}>",
                "EnumAttribute(${enum.typeName}.byString)"
            )
        }
    }
}
