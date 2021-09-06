package org.carrat.web.builder

import org.carrat.context.Context
import org.carrat.web.builder.fragment.Fragment
import org.carrat.web.builder.fragment.ListFragment
import org.carrat.web.builder.html.AnyTagConsumer
import org.carrat.web.builder.html.RootBuilder
import org.carrat.web.builder.html.TagConsumer

internal class DefaultRootBuilder<C>(
    context: Context,
    private val block: TagConsumer<C>.() -> Unit
) : AbstractListBuilder<Fragment, C>(context) {

    override fun executeBlock() {
        block()
    }

    override fun doBuild(
        children: Array<Fragment>,
        mountList: Array<() -> Unit>,
        unmountList: Array<() -> Unit>
    ): Fragment {
        return ListFragment(children, mountList, unmountList)
    }
}