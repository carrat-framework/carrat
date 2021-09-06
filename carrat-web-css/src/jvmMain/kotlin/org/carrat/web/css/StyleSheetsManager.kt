package org.carrat.web.css

import org.carrat.experimental.CarratExperimental
import org.carrat.web.builder.html.*

@CarratExperimental
internal actual class StyleSheetsManager {
    private val importedStyleSheetsUrls = mutableMapOf<String, StyleSheetImportMethod>()
    private val importedStyleSheets = LinkedHashSet<StyleSheet>()
    private val styleString = StringBuilder()

    fun TagConsumer<Head>.injectStyleSheets() {
        val (links, imports) = importedStyleSheetsUrls.entries.partition { it.value == StyleSheetImportMethod.LINK }
        for (link in links) {
            link {
                attributes {
                    rel = LinkType.stylesheet
                    href = link.key
                    type = ContentType.textCss
                }
            }
        }
        unsafeTag(Style) {

            for (import in imports) {
                //TODO: Escape URL - https://developer.mozilla.org/en-US/docs/Web/CSS/url()#values
                appendLine( "@import url('${import.key}');")
            }
            for (styleSheet in importedStyleSheets) {
                append(styleSheet.render())
            }
            append(styleString.toString())
        }
    }

    actual fun importStyleSheet(url: String, method: StyleSheetImportMethod) {
        importedStyleSheetsUrls[url] = method
    }

    actual fun importStyleSheet(styleSheet: StyleSheet) {
        importedStyleSheets.add(styleSheet)
    }

    actual fun injectUnsafeStyle(style: String) {
        styleString.append(style)
    }

    actual fun injectUnsafeStyle(style: Appendable.() -> Unit) {
        styleString.style()
    }
}
