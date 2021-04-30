package org.carrat.model

public interface MutableReference<T> : Reference<T> {
    override var value : T
}
