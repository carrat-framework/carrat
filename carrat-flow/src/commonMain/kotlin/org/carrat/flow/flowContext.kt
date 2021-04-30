package org.carrat.flow

import org.carrat.context.Contextual
import org.carrat.context.HasContext

private val flowContext: Contextual<Flow> = Contextual(Flow())

public val HasContext.flow : Flow get() = context[flowContext]
