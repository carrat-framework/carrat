package org.carrat.web.builder

import org.carrat.web.fragments.Fragment
import org.carrat.context.Context
import org.carrat.experimental.CarratExperimental

@CarratExperimental
public fun Context.build(block: CBlock): Fragment {
    return build(block, this)
}

@CarratExperimental
public fun Context.render(block: CBlock, appendable: Appendable) : Unit = TODO()

