package org.carrat.web.builder

public fun clientOnly() {
    throw IllegalStateException("This code can be executed only on client side.")
}
