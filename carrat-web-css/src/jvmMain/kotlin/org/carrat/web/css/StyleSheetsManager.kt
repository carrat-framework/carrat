package org.carrat.web.css

import kotlinx.html.HEAD
import kotlinx.html.link
import kotlinx.html.style
import org.carrat.experimental.CarratExperimental

@CarratExperimental
internal actual class StyleSheetsManager {
    private val importedStyleSheetsUrls = mutableMapOf<String, StyleSheetImportMethod>()
    private val importedStyleSheets = LinkedHashSet<StyleSheet>()

    fun HEAD.injectStyleSheets() {
        val (links, imports) = importedStyleSheetsUrls.entries.partition { it.value == StyleSheetImportMethod.LINK }
        for(link in links) {
            link {
                rel="StyleSheet"
                href=link.key
                type="text/css"
            }
        }
        style {
            for (import in imports) {
                //TODO: Escape URL - https://developer.mozilla.org/en-US/docs/Web/CSS/url()#values
                +"@import url('${import.key}');\n"
            }
            for (styleSheet in importedStyleSheets) {
                +styleSheet.render()
            }
        }
    }

    actual fun importStyleSheet(url: String, method: StyleSheetImportMethod) {
        importedStyleSheetsUrls.put(url, method)
    }

    actual fun importStyleSheet(styleSheet: StyleSheet) {
        importedStyleSheets.add(styleSheet)
    }
}
