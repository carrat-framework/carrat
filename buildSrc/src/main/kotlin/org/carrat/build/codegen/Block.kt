package org.carrat.build.codegen

class Block(private val emitter: Emitter) : FunctionBody {
    override fun IndentedAppendable.emit() {
        appendLine(" {")
        indented {
            appendIndent()
            emitter()
        }
        appendIndentedLine("}")
    }
}