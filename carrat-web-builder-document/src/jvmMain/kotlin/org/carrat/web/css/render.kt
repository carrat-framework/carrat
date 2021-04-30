package org.carrat.web.css

import kotlinx.html.*
import org.carrat.web.builder.build
import org.carrat.context.Context
import org.carrat.web.builder.document.DocumentTemplate
import org.carrat.experimental.CarratExperimental
import org.carrat.web.fragments.render

@CarratExperimental
public fun TagConsumer<*>.render(template: DocumentTemplate, context: Context, jsPath: String?) {
    val headFragment = context.build(template.head)
    val bodyFragment = context.build(template.body)

    html {
        head {
            render(headFragment)
            injectStyleSheets(context)
            if (jsPath != null) {
                script(src = jsPath) {}
            }
        }
        body {
            render(bodyFragment)
        }
    }
}
