package org.carrat.build.codegen

class ExpressionFunctionBody(private val expression: Emitter) : FunctionBody {
    override fun IndentedAppendable.emit() {
        append(" = ")
        indented {
            expression()
            appendLine()
        }
    }
}