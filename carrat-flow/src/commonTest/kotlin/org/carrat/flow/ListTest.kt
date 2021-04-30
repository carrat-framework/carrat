package org.carrat.flow

import org.carrat.flow.list.list
import kotlin.test.*

class ListTest {
    @Test
    fun canReadListObservable() {
        val flow = Flow(ImmediateDispatcher)
        val list = flow.list(listOf(1))
        assertEquals(1, list.size)
        assertTrue(list.contains(1))
        assertFalse(list.contains(2))
        assertTrue(list.containsAll(setOf()))
        assertTrue(list.containsAll(setOf(1)))
        assertFalse(list.containsAll(setOf(2)))
        assertFalse(list.containsAll(setOf(1, 2)))
        assertEquals(1, list[0])
        assertFailsWith<IndexOutOfBoundsException> {
            list[1]
        }
        assertEquals(0, list.indexOf(1))
        assertEquals(-1, list.indexOf(2))

    }
}
