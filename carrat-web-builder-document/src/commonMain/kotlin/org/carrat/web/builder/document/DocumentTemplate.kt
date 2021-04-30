package org.carrat.web.builder.document

import org.carrat.web.builder.CBlock
import org.carrat.experimental.CarratExperimental

@CarratExperimental
public class DocumentTemplate(
    public val head: CBlock,
    public val body: CBlock
)
