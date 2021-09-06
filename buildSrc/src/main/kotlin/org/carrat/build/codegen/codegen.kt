package org.carrat.build.codegen

fun IndentedAppendable.`package`(name : String) {
    append("package ")
    append(name)
    appendLine()
}

fun IndentedAppendable.`import`(name : String) {
    append("import ")
    append(name)
    appendLine()
}

fun IndentedAppendable.classLike(
    modifiers: Set<Modifier>,
    spec : String,
    parameters : List<String>,
    delegationSpecifiers : List<String>,
    body : (IndentedAppendable.()->Unit)?
) {
    appendIndent()
    modifiers.forEach {
        append(it.string)
        append(" ")
    }
    append(spec)
    if(parameters.isNotEmpty()) {
        append("(")
        increaseIndent()
        var first = true
        parameters.forEach {
            if(first) {
                first = false
            } else {
                append(",")
            }
            appendLine()
            appendIndent()
            append(it)
        }
        decreaseIndent()
        appendLine()
        appendIndent()
        append(")")
    }
    if(delegationSpecifiers.isNotEmpty()) {
        append(" : ")
        var first = true
        delegationSpecifiers.forEach {
            if(first) {
                first = false
            } else {
                append(",")
            }
            append(it)
        }
    }
    if(body != null) {
        append(" {")
        increaseIndent()
        appendLine()
        body()
        decreaseIndent()
        appendIndent()
        append("}")
    }

    appendLine()
}

fun IndentedAppendable.`class`(
    modifiers: Set<Modifier>,
    name : String,
    parameters : List<String>,
    delegationSpecifiers : List<String>,
    body : (IndentedAppendable.()->Unit)?
) = classLike(modifiers, "class $name", parameters, delegationSpecifiers, body)

fun IndentedAppendable.enum(
    modifiers: Set<Modifier>,
    name : String,
    parameters : List<String>,
    delegationSpecifiers : List<String>,
    body : (IndentedAppendable.()->Unit)?
) = classLike(modifiers, "enum class $name", parameters, delegationSpecifiers, body)

fun IndentedAppendable.`interface`(
    modifiers: Set<Modifier>,
    name : String,
    delegationSpecifiers : List<String>,
    body : (IndentedAppendable.()->Unit)? = null
) = classLike(modifiers, "interface $name", emptyList(), delegationSpecifiers, body)

fun IndentedAppendable.companionObject(
    modifiers: Set<Modifier>,
    delegationSpecifiers : List<String> = emptyList(),
    body : (IndentedAppendable.()->Unit)?
) = classLike(modifiers, "companion object", emptyList(), delegationSpecifiers, body)

fun IndentedAppendable.`object`(
    modifiers: Set<Modifier>,
    name : String,
    delegationSpecifiers : List<String>,
    body : (IndentedAppendable.()->Unit)? = null
) = classLike(modifiers, "object $name", emptyList(), delegationSpecifiers, body)

fun IndentedAppendable.variable(
    modifiers: Set<Modifier>,
    valVar : String,
    name : String,
    type : String?,
    value : String?,
    getter: Getter? = null,
    setter: Setter? = null
) {
    appendIndent()
    modifiers.forEach {
        append(it.string)
        append(" ")
    }
    append(valVar)
    append(" ")
    append(name)
    if(type != null) {
        append(" : ")
        append(type)
    }
    if(value != null) {
        append(" = ")
        append(value)
    }
    appendLine()
    if(getter != null) {
        indented {
            appendIndent()
            getter.modifiers.forEach {
                append(it.string)
                append(" ")
            }
            append("get()")
            if(getter.type != null) {
                append(" : ")
                append(getter.type)
            }
            if(getter.body != null) {
                with(getter.body) {
                    emit()
                }
            }
        }
    }
    if(setter != null) {
        indented {
            appendIndent()
            setter.modifiers.forEach {
                append(it.string)
                append(" ")
            }
            append("set(")
            append(setter.functionValueParameterWithOptionalType)
            append(")")
            if(setter.type != null) {
                append(" : ")
                append(setter.type)
            }
            if(setter.body != null) {
                with(setter.body) {
                    emit()
                }
            }
        }
    }
}

fun IndentedAppendable.`val`(
    modifiers: Set<Modifier>,
    name : String,
    type : String? = null,
    value : String? = null
) = variable(modifiers, "val", name, type, value)

fun IndentedAppendable.`var`(
    modifiers: Set<Modifier>,
    name : String,
    type : String? = null,
    value : String? = null,
    getter: Getter? = null,
    setter: Setter? = null
) = variable(modifiers, "var", name, type, value, getter, setter)

private fun IndentedAppendable.funDeclaration(
    modifiers: Set<Modifier>,
    receiver: String?,
    name: String,
    parameters: List<String>,
    type: String?
) {
    appendIndent()
    modifiers.forEach {
        append(it.string)
        append(" ")
    }
    append("fun ")
    if (receiver != null) {
        append(receiver)
        append(".")
    }
    append(Identifier(name).toString())
    append("(")
    if (parameters.isNotEmpty()) {
        increaseIndent()
        var first = true
        parameters.forEach {
            if (first) {
                first = false
            } else {
                append(",")
            }
            appendLine()
            appendIndent()
            append(it)
        }
        appendLine()
        decreaseIndent()
        appendIndent()
    }
    append(")")
    if (type != null) {
        append(" : ")
        append(type)
    }
}

fun IndentedAppendable.`fun`(
    modifiers: Set<Modifier>,
    receiver : String? = null,
    name : String,
    parameters : List<String>,
    type : String? = null,
    value : String? = null
) {
    funDeclaration(modifiers, receiver, name, parameters, type)
    if(value != null) {
        append(" = ")
        append(value)
    }
    appendLine()
}

fun IndentedAppendable.`fun`(
    modifiers: Set<Modifier>,
    receiver : String? = null,
    name : String,
    parameters : List<String>,
    type : String? = null,
    body : (IndentedAppendable.()->Unit)
) {
    funDeclaration(modifiers, receiver, name, parameters, type)
    append(" {")
    increaseIndent()
    appendLine()
    body()
    decreaseIndent()
    appendIndent()
    append("}")
    appendLine()
}
