package org.carrat.web.css

import kotlinx.css.CSSBuilder
import kotlinx.css.RuleSet
import org.carrat.context.HasContext

public fun HasContext.importStyleSheet(url: String, method: StyleSheetImportMethod = StyleSheetImportMethod.STYLE_IMPORT) {
    context.get(styleSheetsManager).importStyleSheet(url, method)
}

public fun HasContext.importStyleSheet(styleSheet: StyleSheet) {
    context.get(styleSheetsManager).importStyleSheet(styleSheet)
}

public fun HasContext.injectUnsafeStyle(style : String) {
    context.get(styleSheetsManager).injectUnsafeStyle(style)
}

public fun HasContext.injectUnsafeStyle(style : Appendable.()->Unit) {
    context.get(styleSheetsManager).injectUnsafeStyle(style)
}

public fun HasContext.injectStyle(style : RuleSet) {
    val cssBuilder = CSSBuilder()
    cssBuilder.style()
    context.get(styleSheetsManager).injectUnsafeStyle(cssBuilder.toString())
}

public fun HasContext.injectStyle(className : String, style : RuleSet) {
    val cssBuilder = CSSBuilder()
    cssBuilder.apply {
        ".${className}" {
            style()
        }
    }
    context.get(styleSheetsManager).injectUnsafeStyle(cssBuilder.toString())
}
