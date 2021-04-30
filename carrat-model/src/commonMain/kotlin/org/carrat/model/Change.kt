package org.carrat.model

import kotlinx.serialization.Serializable

@Serializable
public data class Change<out Value>(
    val oldValue: Value,
    val newValue: Value
)
