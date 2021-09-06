package org.carrat.build.codegen

sealed fun interface FunctionBody {
    fun IndentedAppendable.emit()
}