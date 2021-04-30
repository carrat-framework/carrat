package org.carrat.simple.model

import org.carrat.experimental.CarratExperimental
import org.carrat.model.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@CarratExperimental
public fun <R, T> Property<T>.asReadWriteProperty(): ReadWriteProperty<R, T> = object :
    ReadWriteProperty<R, T> {
    override fun getValue(thisRef: R, property: KProperty<*>): T = this@asReadWriteProperty.value

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        this@asReadWriteProperty.value = value
    }
}
