package org.carrat.build.codegen

interface IndentedAppendable : Appendable {
    val currentIndent : String
    val defaultIndentIncrease : String

    fun increaseIndent(increase : String = defaultIndentIncrease)
    fun decreaseIndent()
}

fun IndentedAppendable.appendIndent() {
    append(currentIndent)
}

fun IndentedAppendable.appendIndentedLine(csq : CharSequence = "") {
    appendIndent()
    appendLine(csq)
}

inline fun IndentedAppendable.indented(emitter: Emitter) {
    increaseIndent()
    emitter()
    decreaseIndent()
}