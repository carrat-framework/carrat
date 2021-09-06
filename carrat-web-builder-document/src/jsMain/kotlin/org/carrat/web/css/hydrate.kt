package org.carrat.web.css

import org.carrat.web.builder.build
import org.carrat.context.Context
import org.carrat.web.builder.document.DocumentTemplate
import org.carrat.web.builder.fragment.hydrate
import org.carrat.web.webapi.Document

public fun Document.hydrate(template: DocumentTemplate, context: Context) {
    val headFragment = context.build(template.head)
    val bodyFragment = context.build(template.body)
    head!!.hydrate(headFragment)
    body!!.hydrate(bodyFragment)
}
