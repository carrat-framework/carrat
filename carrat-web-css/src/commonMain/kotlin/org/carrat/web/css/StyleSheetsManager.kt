package org.carrat.web.css

import org.carrat.context.Contextual

internal expect class StyleSheetsManager() {
    fun importStyleSheet(url: String, method: StyleSheetImportMethod = StyleSheetImportMethod.STYLE_IMPORT)
    fun importStyleSheet(styleSheet: StyleSheet)
    fun injectUnsafeStyle(style : String)
    fun injectUnsafeStyle(style : Appendable.()->Unit)
}

internal val styleSheetsManager: Contextual<StyleSheetsManager> by lazy { Contextual(StyleSheetsManager()) }
