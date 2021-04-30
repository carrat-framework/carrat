package org.carrat.flow

import kotlinx.coroutines.Runnable

class EventsCollector<Event> {
    private var events : MutableList<Event>? = null

    fun emit(event: Event) {
        val events = events
        if(events != null) {
            events += event
        } else {
            throw IllegalStateException("Events are currently not being collected.")
        }
    }

    fun collect(runnable: ()->Unit) : List<Event> {
        if(events != null) {
            throw IllegalStateException("Events are already being collected.")
        }
        try {
            val currentEvents = mutableListOf<Event>()
            events = currentEvents
            runnable()
            return currentEvents
        } finally {
            events = null
        }
    }
}
