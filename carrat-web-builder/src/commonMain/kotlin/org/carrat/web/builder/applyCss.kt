package org.carrat.web.builder

import kotlinx.css.CSSBuilder
import kotlinx.css.hyphenize
import org.carrat.web.builder.html.CoreAttributeGroup
import org.carrat.web.builder.html.`class`
import org.carrat.web.builder.html.style

internal fun CoreAttributeGroup<*>.applyCss(css: CSSBuilder) {
    if (css.rules.isNotEmpty() || css.multiRules.isNotEmpty()) {
        TODO("Not allowed, throw exception, link help page.")
    }

    if (css.declarations.isNotEmpty()) {
        style = renderDeclarations(css.declarations)
    }
    if (css.classes.isNotEmpty()) {
        `class` = css.classes.joinToString(separator = " ")
    }
}

private fun renderDeclarations(declarations: Map<String, Any>): String {
    return declarations.map { (key, value) -> "${key.hyphenize()}:$value" }.joinToString(";")
}
