package org.carrat.context

import org.carrat.experimental.CarratExperimental

public class Context constructor(
    @CarratExperimental
    private val parent: Context?,
    private val overridesInit: Map<Contextual<*>, Context.()->Any>
) : HasContext {
    private val overrides: MutableMap<Contextual<*>, Any> = mutableMapOf()

    public operator fun <T : Any> get(contextual: Contextual<T>): T {
        val existingContextual = overrides[contextual]
        val contextualValue = if(existingContextual != null) {
            existingContextual
        } else {
            val computedContextual = overridesInit[contextual]?.invoke(this)
            if(computedContextual != null) {
                overrides[contextual] = computedContextual
            }
            computedContextual
        }
        return if (contextualValue != null) {
            @Suppress("UNCHECKED_CAST")
            contextualValue as T
        } else {
            if(parent != null) {
                return parent.get(contextual)
            } else {
                val computedContextual = contextual.defaultValueFactory(this)
                overrides[contextual] = computedContextual
                computedContextual
            }
        }
    }

    override val context: Context
        get() = this
}

@CarratExperimental
public fun <T : Any> Context.provide(contextual: Contextual<T>, contextValue: T): Context {
    return Context(this, mapOf(contextual to {contextValue}))
}

public fun RootContext(contextValues : Map<Contextual<*>, Context.()->Any>): Context = Context(null, contextValues)
