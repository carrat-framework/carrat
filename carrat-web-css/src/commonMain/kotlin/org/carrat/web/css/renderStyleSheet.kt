package org.carrat.web.css

import kotlinx.css.CSSBuilder
import kotlinx.css.RuleSet
import kotlinx.css.hyphenize

internal fun StyleSheet.render(): String {
    val sb = StringBuilder()
    sb.render({
        this@render.globals.forEach { +it }
        this@render.classes.forEach { classEntry ->
            ".${classEntry.first}" {
                +classEntry.second
            }
        }
    }, null)
    return sb.toString()
}

internal fun Appendable.render(ruleSet: RuleSet, context: String?) {
    val cssBuilder = CSSBuilder(allowClasses = false)
    cssBuilder.apply(ruleSet)
    val declarations = cssBuilder.declarations
    if (context != null) {
        if (declarations.isNotEmpty()) {
            append(context)
            append("{")
            for (declaration in declarations) {
                append(declaration.key.hyphenize())
                append(":")
                append(declaration.value.toString())
                append(";")
            }
            append("}")
        }
    } else {
        for (declaration in declarations) {
            append(declaration.key.hyphenize())
            append(":")
            append(declaration.value.toString())
            append(";")
        }
    }

    for (rule in cssBuilder.rules) {
        val selector = rule.selector
        when {
            selector.startsWith("&") -> {
                render(rule.block, context + selector.substring(1))
            }
            selector.startsWith("@media") -> {
                if (context != null) {
                    append(selector)
                    append("{")
                    append(context)
                    append("{")
                    render(rule.block, null)
                    append("}")
                    append("}")
                } else {
                    append(selector)
                    append("{")
                    render(rule.block, null)
                    append("}")
                }
            }
            else -> {
                val effectiveSelector = if (context == null) selector else "${context} ${selector}"
                render(rule.block, effectiveSelector)
            }
        }
    }
}
