package org.carrat.flow

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.carrat.flow.impl.*

// We use LinkedHash(Set|Map) inside this implementation, to make it deterministic.
internal class FlowImpl(
    private val dispatcher: CoroutineDispatcher
) : Flow {
    internal var generation: Generation = Generation(0)
    internal var actionReceiver: ActionReceiverImpl? = null

    //TODO: Maybe more efficient queue implementation?
    private val actionQueue = mutableListOf<Action>()
    internal var reevaluationQueue = ReevaluationQueue()
    internal val changed = LinkedHashSet<Store<*, *>>()
    internal var deferredSubscriptionCalls = LinkedHashMap<SubscriptionId, SubscriptionCall<*>>()
    internal var deferredMutationSubscriptionCalls = LinkedHashMap<SubscriptionId, MutationSubscriptionCall<*>>()
    private var dispatchJob: Job? = null
    private var reevaluating = false
    private var queryReceiver: QueryReceiverImpl? = null

    override fun apply(action: Action) {
        actionQueue += action
        dispatch()
    }

    private fun dispatch() {
        if (dispatchJob == null) {
            dispatchJob = MainScope().launch(dispatcher) {
                processActionQueue()
                dispatchJob = null
            }
            if (dispatchJob?.isCompleted == true) {
                dispatchJob = null
            }
        }
    }

    private fun processActionQueue() {
        withActionReceiver {
            // Execute actions
            while (this@FlowImpl.actionQueue.isNotEmpty()) {
                this@FlowImpl.actionQueue.removeFirst().apply { invoke() }
            }
        }
        generation = generation.next()
        reevaluating = true
        val changedIterator = changed.iterator()
        while (changedIterator.hasNext()) {
            changedIterator.next().flush()
            changedIterator.remove()
        }
        reevaluate(null)
        reevaluating = false
        // Call subscribers
        val deferredSubscriptionCalls = this@FlowImpl.deferredSubscriptionCalls
        val deferredMutationSubscriptionCalls = this@FlowImpl.deferredMutationSubscriptionCalls
        this.deferredSubscriptionCalls = LinkedHashMap()
        this.deferredMutationSubscriptionCalls = LinkedHashMap()
        withQueryReceiver {
            deferredSubscriptionCalls.values.forEach { it.invoke() }
            deferredMutationSubscriptionCalls.values.forEach { it.invoke() }
        }
    }

    internal fun reevaluate(depth: Int? = null) {
        if (reevaluating) {
            while (reevaluationQueue.hasLower(depth)) {
                val toReevaluate = reevaluationQueue.pop()
                toReevaluate.reevaluate()
            }
        }
    }

    private fun withActionReceiver(task: ActionReceiverImpl.() -> Unit) {
        if (actionReceiver == null) {
            actionReceiver = ActionReceiverImpl(this)
            actionReceiver!!.task()
            actionReceiver = null
        } else {
            actionReceiver!!.task()
        }
    }


    //TODO: Change to withTracker(tracker: LinkedHashSet<Dependency<*>>, task)
    internal fun <Result> withQueryReceiver(
        queryReceiver: QueryReceiverImpl,
        task: QueryReceiver.() -> Result
    ): Result {
        if (actionReceiver != null) {
            throw IllegalStateException("Querying flow during action is not allowed.")
        }
        fun run() = queryReceiver.task()
        val queryReceiverBackup = this.queryReceiver
        this.queryReceiver = queryReceiver
        val result = run()
        this.queryReceiver = queryReceiverBackup
        return result
    }

    private fun withQueryReceiver(task: QueryReceiver.() -> Unit) {
        if (actionReceiver != null) {
            throw IllegalStateException("Querying flow during action is not allowed.")
        }
        fun run() = queryReceiver!!.task()
        return if (queryReceiver != null) {
            run()
        } else {
            queryReceiver = QueryReceiverImpl(this@FlowImpl)
            val result = run()
            queryReceiver = null
            result
        }
    }

    override fun <State, Mutation> observable(store: Store<State, Mutation>): Observable<State, Mutation> {
        return ObservableImpl(this, store)
    }

    override fun <Result> query(query: FlowQuery<Result>): Result {
        fun run(): Result = query.run { queryReceiver!!.invoke() }
        return if (queryReceiver != null) {
            run()
        } else {
            queryReceiver = QueryReceiverImpl(this@FlowImpl)
            val result = run()
            queryReceiver = null
            result
        }
    }
}
