package org.carrat.flow.impl

import kotlin.jvm.JvmInline

@JvmInline
internal value class Generation(
    private val value : Long
) : Comparable<Generation> {
    override fun compareTo(other: Generation): Int = value.compareTo(other.value)
    fun next() = Generation(value + 1)
}
