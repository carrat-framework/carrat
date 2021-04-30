package org.carrat.web.css

import org.carrat.context.HasContext

public fun HasContext.importStyleSheet(url: String, method: StyleSheetImportMethod = StyleSheetImportMethod.STYLE_IMPORT) {
    context.get(styleSheetsManager).importStyleSheet(url, method)
}

public fun HasContext.importStyleSheet(styleSheet: StyleSheet) {
    context.get(styleSheetsManager).importStyleSheet(styleSheet)
}
