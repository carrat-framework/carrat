package org.carrat.web.builder

public fun clientOnly(): Nothing {
    throw IllegalStateException("This code can be executed only on client side.")
}
