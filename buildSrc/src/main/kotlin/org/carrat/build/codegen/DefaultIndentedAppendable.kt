package org.carrat.build.codegen

class DefaultIndentedAppendable(
    private val delegate : Appendable,
    override val defaultIndentIncrease: String = "    ",
    currentIndent: String = ""
    ) : IndentedAppendable, Appendable by delegate {
    private val stack = ArrayList<String>()
    private var _currentIndent: String = currentIndent
    override val currentIndent: String
        get() = _currentIndent

    override fun increaseIndent(increase: String) {
        stack.add(_currentIndent)
        _currentIndent += increase
    }

    override fun decreaseIndent() {
        _currentIndent = stack.removeLast()
    }
}
