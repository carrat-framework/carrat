package org.carrat.web.css

import kotlinx.html.HEAD
import org.carrat.context.Context

public fun HEAD.injectStyleSheets(context : Context){
    with(context.get(styleSheetsManager)) {
        this@injectStyleSheets.injectStyleSheets()
    }
}
