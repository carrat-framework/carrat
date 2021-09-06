package org.carrat.web.builder

import org.carrat.context.Context
import org.carrat.experimental.CarratExperimental
import org.carrat.web.builder.fragment.Fragment
import org.carrat.web.builder.html.Block
import org.carrat.web.builder.html.DefaultHtmlWriter

@CarratExperimental
public fun <C> Context.build(block: Block<C>): Fragment {
    val builder = DefaultRootBuilder(this, block)
    return builder.build()
}

@CarratExperimental
public fun <C> Context.render(block: Block<C>, out: Appendable) {
    with(build(block)) {
        DefaultHtmlWriter(out).render()
    }
}
