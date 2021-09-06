package org.carrat.web.css

import org.carrat.web.builder.build
import org.carrat.context.Context
import org.carrat.web.builder.document.DocumentTemplate
import org.carrat.experimental.CarratExperimental
import org.carrat.web.builder.html.*

@CarratExperimental
internal fun TagConsumer<HtmlConsumer>.render(template: DocumentTemplate, context: Context, jsPath: String?) {
    val headFragment = context.build(template.head)
    val bodyFragment = context.build(template.body)

    html {
        head {
            append(headFragment)
            injectStyleSheets(context)
            if (jsPath != null) {
                script {
                    attributes {
                        src = jsPath
                    }
                }
            }
        }
        body {
            append(bodyFragment)
        }
    }
}

@CarratExperimental
public fun Appendable.render(template: DocumentTemplate, context: Context, jsPath: String?) {
    val fragment = context.build<HtmlConsumer> {
        render(template, context, jsPath)
    }
    with(DefaultHtmlWriter(this)) {
        with(fragment) { render() }
    }
}