package org.carrat.web.builder

import org.carrat.context.Context
import org.carrat.web.builder.fragment.Fragment

internal abstract class AbstractBuilder<F : Fragment>(
    override val context: Context
) : Builder {
    private val mountList = mutableListOf<() -> Unit>()
    private val unmountList = mutableListOf<() -> Unit>()

    override fun onMount(callback: () -> Unit) {
        mountList += callback
    }

    override fun onUnmount(callback: () -> Unit) {
        unmountList += callback
    }

    protected abstract fun executeBlock()

    protected abstract fun doBuild(mountList : Array<() -> Unit>, unmountList : Array<() -> Unit>) : F

    fun build() : F {
        executeBlock()
        return doBuild(mountList.toTypedArray(), unmountList.toTypedArray())
    }
}