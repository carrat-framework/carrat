package org.carrat.flow.impl

/**
 * A fair(for determinism) priority queue implementation.
 */
internal class ReevaluationQueue {
    private val queue: MutableList<Node> = mutableListOf()

    fun push(value: MappedObservable<*>) {
        val depth = value.lastDepth
        val foundIndex = queue.binarySearch { depth.compareTo(it.depth) }
        val node = if (foundIndex < 0) {
            val newNode = Node(depth)
            queue.add(-foundIndex-1, newNode)
            newNode
        } else {
            queue[foundIndex]
        }
        node.elements.add(value)
    }

    fun pop(): MappedObservable<*> {
        val elements = queue.first().elements
        val iterator = elements.iterator()
        val first = iterator.next()
        iterator.remove()
        if (elements.isEmpty()) {
            queue.removeFirst()
        }
        return first
    }

    fun isNotEmpty(): Boolean = queue.isNotEmpty()
    fun hasLower(depth : Int?) : Boolean = if(depth == null) {
        isNotEmpty()
    } else {
        isNotEmpty() && queue.first().depth <= depth
    }
}

private class Node(
    val depth: Int
) {
    val elements: LinkedHashSet<MappedObservable<*>> = LinkedHashSet()
}
