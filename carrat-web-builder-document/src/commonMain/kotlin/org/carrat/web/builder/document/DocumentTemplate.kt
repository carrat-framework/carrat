package org.carrat.web.builder.document

import org.carrat.experimental.CarratExperimental
import org.carrat.web.builder.html.Block
import org.carrat.web.builder.html.Body
import org.carrat.web.builder.html.Head

@CarratExperimental
public class DocumentTemplate(
    public val head: Block<Head>,
    public val body: Block<Body>
)
