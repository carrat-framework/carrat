package org.carrat.web.builder

import kotlinx.css.CSSBuilder
import kotlinx.css.hyphenize
import kotlinx.html.Tag

internal fun Tag.applyCss(css: CSSBuilder) {
    if (css.rules.isNotEmpty() || css.multiRules.isNotEmpty()) {
        TODO("Not allowed, throw exception, link help page.")
    }
    if (css.declarations.isNotEmpty()) {
        attributes["style"] = renderDeclarations(css.declarations)
    }
    if (css.classes.isNotEmpty()) {
        attributes["class"] = css.classes.joinToString(separator = " ")
    }
}

private fun renderDeclarations(declarations : Map<String, Any>) : String {
    return declarations.map { (key, value) -> "${key.hyphenize()}:$value" }.joinToString(";")
}
