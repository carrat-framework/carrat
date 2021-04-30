package org.carrat.flow

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlin.coroutines.CoroutineContext

object ImmediateDispatcher : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        block.run()
    }
}

object DeferredDispatcher : CoroutineDispatcher() {
    private val queue : MutableList<Runnable> = mutableListOf()

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        queue.add(block)
    }

    fun dispatch() {
        while (queue.isNotEmpty()) {
            queue.removeFirst().run()
        }
    }
}
