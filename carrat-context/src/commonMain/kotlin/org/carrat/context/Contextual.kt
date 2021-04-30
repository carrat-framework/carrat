package org.carrat.context

import org.carrat.experimental.CarratExperimental
import kotlin.reflect.KProperty

@CarratExperimental
public class Contextual<out T : Any>(internal val defaultValueFactory: Context.() -> T) {
    public operator fun getValue(thisRef: HasContext, property: KProperty<*>): T {
        return thisRef.context[this]
    }
}

@Suppress("FunctionName")
@CarratExperimental
public fun <T : Any> Contextual(defaultValue: T): Contextual<T> = Contextual { defaultValue }
