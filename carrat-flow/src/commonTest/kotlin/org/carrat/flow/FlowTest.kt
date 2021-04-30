package org.carrat.flow

import org.carrat.flow.property.PropertyStore
import kotlin.test.Test
import kotlin.test.assertEquals

class FlowTest {
    @Test
    fun canReadStore() {
        val flow = Flow(ImmediateDispatcher)
        val initialValue = 1
        val testProperty = flow.observable(PropertyStore(initialValue))
        val currentValue = flow.query {
            testProperty.get()
        }
        assertEquals(initialValue, currentValue)
    }

    @Test
    fun canMutateStore() {
        val flow = Flow(ImmediateDispatcher)
        val initialValue = 1
        val secondValue = 2
        val testProperty = flow.observable(PropertyStore(initialValue))
        flow.apply {
            testProperty.set(secondValue)
        }
        val currentValue = flow.query {
            testProperty.get()
        }
        assertEquals(secondValue, currentValue)
    }

    @Test
    fun canSubscribeStore() {
        val eventsCollector = EventsCollector<Int>()
        val flow = Flow(ImmediateDispatcher)
        val initialValue = 1
        val secondValue = 2
        val testProperty = flow.observable(PropertyStore(initialValue))
        val events = eventsCollector.collect {
            flow.query {
                testProperty.subscribeValue { value, _ ->
                    eventsCollector.emit(value)
                }
            }
            flow.apply {
                testProperty.set(secondValue)
            }
        }
        assertEquals(listOf(secondValue), events)
    }

    @Test
    fun subscriptionsAreDeferred() {
        val eventsCollector = EventsCollector<Int>()
        val flow = Flow(DeferredDispatcher)
        val initialValue = 1
        val secondValue = 2
        val thirdValue = 3
        val testProperty = flow.observable(PropertyStore(initialValue))
        flow.query {
            testProperty.subscribeValue { value, _ ->
                eventsCollector.emit(value)
            }
        }
        flow.apply {
            testProperty.apply(SetValue(secondValue))
            testProperty.apply(SetValue(thirdValue))
        }
        val events = eventsCollector.collect {
            DeferredDispatcher.dispatch()
        }
        assertEquals(listOf(thirdValue), events)
    }

    @Test
    fun mapTest() {
        val flow = Flow(DeferredDispatcher)
        val initialValue = 1
        val testProperty = flow.observable(PropertyStore(initialValue))
        val mappedProperty = flow.query { testProperty.lazyMap(GetValue()) { -it } }
        val mappedPropertyValue = flow.query { mappedProperty.query(GetValue()) }
        assertEquals(-initialValue, mappedPropertyValue)
    }

    @Test
    fun mapUpdateTest() {
        val flow = Flow(ImmediateDispatcher)
        val initialValue = 1
        val secondValue = 2
        val testProperty = flow.observable(PropertyStore(initialValue))
        val mappedProperty = flow.query { testProperty.lazyMap { -it } }
        flow.apply {
            testProperty.set(secondValue)
        }
        val mappedPropertyValue = flow.query { mappedProperty.get() }
        assertEquals(-secondValue, mappedPropertyValue)
    }

    @Test
    fun canSubscribeMapped() {
        val eventsCollector = EventsCollector<Int>()
        val flow = Flow(ImmediateDispatcher)
        val initialValue = 1
        val secondValue = 2
        val testProperty = flow.observable(PropertyStore(initialValue))
        val mappedProperty = flow.query { testProperty.lazyMap { -it } }
        flow.query {
            mappedProperty.subscribeValue { value, _ ->
                eventsCollector.emit(value)
            }
        }
        val events = eventsCollector.collect {
            flow.apply {
                testProperty.set(secondValue)
            }
        }
        assertEquals(listOf(-secondValue), events)
    }

    @Test
    fun mappedWontEmitWhenValueHaventChanged() {
        val eventsCollector = EventsCollector<Int>()
        val flow = Flow(ImmediateDispatcher)
        val initialValue = 1
        val secondValue = 2
        val thirdValue = 3
        val testProperty = flow.observable(PropertyStore(initialValue))
        val mappedProperty = flow.query { testProperty.lazyMap { it / 2 } }
        flow.query {
            mappedProperty.subscribeValue { value, _ ->
                eventsCollector.emit(value)
            }
        }
        val events = eventsCollector.collect {
            flow.apply {
                testProperty.set(secondValue)
            }
        }
        flow.apply {
            testProperty.set(thirdValue)
        }
        assertEquals(listOf(secondValue, thirdValue).map { it / 2 }.distinct(), events)
    }

    @Test
    fun mappedWontEmitWhenValueHaventChanged2() {
        val eventsCollector = EventsCollector<Int>()
        val flow = Flow(DeferredDispatcher)
        val aProperty = flow.observable(PropertyStore(1))
        val bProperty = flow.observable(PropertyStore(1))
        val mappedProperty = flow.query {
            lazyMap {
                aProperty.get() + bProperty.get()
            }
        }
        flow.query {
            mappedProperty.subscribeValue { value, _ ->
                eventsCollector.emit(value)
            }
        }
        flow.apply {
            aProperty.set(0)
        }
        flow.apply {
            bProperty.set(2)
        }
        val events = eventsCollector.collect {
            DeferredDispatcher.dispatch()
        }
        assertEquals(emptyList(), events)
    }

    @Test
    fun mappedWillEmitWhenValueHaveChanged() {
        val eventsCollector = EventsCollector<Int>()
        val flow = Flow(DeferredDispatcher)
        val aProperty = flow.observable(PropertyStore(1))
        val bProperty = flow.observable(PropertyStore(1))
        val mappedProperty = flow.query {
            lazyMap {
                aProperty.get() + bProperty.get()
            }
        }
        flow.query {
            mappedProperty.subscribeValue { value, _ ->
                eventsCollector.emit(value)
            }
        }
        flow.apply {
            aProperty.set(0)
        }
        val events = eventsCollector.collect {
            DeferredDispatcher.dispatch()
        }
        assertEquals(listOf(1), events)
    }

    @Test
    fun mappedCanHandleDependenciesChange() {
        val eventsCollector = EventsCollector<Int>()
        val flow = Flow(ImmediateDispatcher)
        val aProperty = flow.observable(PropertyStore(1))
        val bProperty = flow.observable(PropertyStore(1))
        val cProperty = flow.observable(PropertyStore(1))
        val mappedProperty = flow.query {
            lazyMap {
                if (aProperty.query(GetValue()) == 1) {
                    bProperty.get()
                } else {
                    cProperty.get()
                }
            }
        }
        flow.query {
            mappedProperty.subscribeValue { value, _ ->
                eventsCollector.emit(value)
            }
        }
        flow.apply {
            aProperty.set(2)
        }
        val events = eventsCollector.collect {
            flow.apply {
                cProperty.set(2)
            }
        }
        assertEquals(listOf(2), events)
    }

    @Test
    fun mappedCanHandleDependenciesChange2() {
        val eventsCollector = EventsCollector<Int>()
        val flow = Flow(ImmediateDispatcher)
        val aProperty = flow.observable(PropertyStore(1))
        val bProperty = flow.observable(PropertyStore(1))
        val cProperty = flow.observable(PropertyStore(1))
        val mappedProperty = flow.query {
            lazyMap {
                if (aProperty.get() == 1) {
                    bProperty.get()
                } else {
                    cProperty.get()
                }
            }
        }
        flow.apply {
            aProperty.set(2)
        }
        flow.query {
            mappedProperty.subscribe(GetValue()) { value, _ ->
                eventsCollector.emit(value)
            }
        }
        val events = eventsCollector.collect {
            flow.apply {
                cProperty.set(2)
            }
        }
        assertEquals(listOf(2), events)
    }

    @Test
    fun mappedCanHandleDependenciesChange3() {
        val eventsCollector = EventsCollector<Int>()
        val flow = Flow(ImmediateDispatcher)
        val aProperty = flow.observable(PropertyStore(1))
        val bProperty = flow.observable(PropertyStore(1))
        val cProperty = flow.observable(PropertyStore(1))
        val cc = flow.query { cProperty.lazyMap { it * it } }
        val ccc = flow.query { cc.lazyMap { it * it } }
        val mappedProperty = flow.query {
            lazyMap {
                if (aProperty.get() == 1) {
                    bProperty.get()
                } else {
                    ccc.get()
                }
            }
        }
        flow.apply {
            aProperty.set(2)
        }
        flow.query {
            mappedProperty.subscribe(GetValue()) { value, _ ->
                eventsCollector.emit(value)
            }
        }
        val events = eventsCollector.collect {
            flow.apply {
                cProperty.set(2)
            }
        }
        assertEquals(listOf(16), events)
    }

    @Test
    fun mappedCanHandleDependenciesChange4() {
        val eventsCollector = EventsCollector<Int>()
        val flow = Flow(ImmediateDispatcher)
        val aProperty = flow.observable(PropertyStore(1))
        val bProperty = flow.observable(PropertyStore(1))
        val cProperty = flow.observable(PropertyStore(1))
        val cc = flow.query { cProperty.lazyMap { it * it } }
        val ccc = flow.query { cc.lazyMap { it * it } }
        val mappedProperty = flow.query {
            lazyMap {
                if (aProperty.get() == 1) {
                    bProperty.get()
                } else {
                    ccc.get()
                }
            }
        }
        flow.query {
            mappedProperty.subscribe(GetValue()) { value, _ ->
                eventsCollector.emit(value)
            }
        }
        flow.apply {
            aProperty.set(2)
        }
        val events = eventsCollector.collect {
            flow.apply {
                cProperty.set(2)
            }
        }
        assertEquals(listOf(16), events)
    }

    @Test
    fun mappedCanHandleDependenciesChange5() {
        val eventsCollector = EventsCollector<Int>()
        val flow = Flow(ImmediateDispatcher)
        val aProperty = flow.observable(PropertyStore(1))
        val bProperty = flow.observable(PropertyStore(1))
        val cProperty = flow.observable(PropertyStore(1))
        val cc = flow.query { cProperty.lazyMap { it * it } }
        val ccc = flow.query { cc.lazyMap { it * it } }
        flow.query { ccc.subscribeValue { _, _ -> } }
        val mappedProperty = flow.query {
            lazyMap {
                if (aProperty.get() == 1) {
                    bProperty.get()
                } else {
                    ccc.get()
                }
            }
        }
        flow.query {
            mappedProperty.subscribe(GetValue()) { value, _ ->
                eventsCollector.emit(value)
            }
        }
        flow.apply {
            aProperty.set(2)
        }
        val events = eventsCollector.collect {
            flow.apply {
                cProperty.set(2)
            }
        }
        assertEquals(listOf(16), events)
    }
}
