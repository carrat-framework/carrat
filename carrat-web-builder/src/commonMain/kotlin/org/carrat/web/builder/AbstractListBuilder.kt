package org.carrat.web.builder

import org.carrat.context.Context
import org.carrat.web.builder.fragment.Fragment
import org.carrat.web.builder.fragment.ListFragment
import org.carrat.web.builder.html.AnyTagConsumer
import org.carrat.web.builder.html.RootBuilder
import org.carrat.web.builder.html.TagConsumer

internal abstract class AbstractListBuilder<F : Fragment, Consumer>(
    context: Context
) : AbstractBuilder<F>(context), TagConsumer<Consumer> {
    private val children = mutableListOf<Fragment>()

    override fun append(fragment: Fragment) {
        children += fragment
    }

    final override fun doBuild(mountList: Array<() -> Unit>, unmountList: Array<() -> Unit>): F {
        return doBuild(children.toTypedArray(), mountList, unmountList)
    }

    protected abstract fun doBuild(children : Array<Fragment>, mountList: Array<() -> Unit>, unmountList: Array<() -> Unit>): F
}