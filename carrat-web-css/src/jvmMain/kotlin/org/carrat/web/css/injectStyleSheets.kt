package org.carrat.web.css

import org.carrat.context.Context
import org.carrat.web.builder.html.Head
import org.carrat.web.builder.html.TagConsumer

public fun TagConsumer<Head>.injectStyleSheets(context: Context) {
    with(context.get(styleSheetsManager)) {
        injectStyleSheets()
    }
}
