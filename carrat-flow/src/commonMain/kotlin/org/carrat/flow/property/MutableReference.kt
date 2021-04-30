package org.carrat.flow.property

public interface MutableReference<Value> : Reference<Value> {
    override var value : Value

}
