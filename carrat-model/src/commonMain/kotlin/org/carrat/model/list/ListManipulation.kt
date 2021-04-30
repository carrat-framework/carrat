package org.carrat.model.list

import org.carrat.experimental.LowerBound

public sealed interface ListManipulation<out E> {
    public fun applyTo(target: MutableList<in E>)
    public fun <@LowerBound("E") T> toBasic(list: List<T>): List<BasicListManipulation<T>> =
        normalize().flatMap { it.toBasic(list) }

    public fun normalize(): List<NormalListManipulation<E>>
    public fun <T> map(transform: (E) -> T): ListManipulation<T>
}

public sealed interface NormalListManipulation<out E> : ListManipulation<E> {
    public override fun <@LowerBound("E") T> toBasic(list: List<T>): List<BasicListManipulation<T>>
    override fun normalize(): List<NormalListManipulation<E>> = listOf(this)
    abstract override fun <T> map(transform: (E) -> T): NormalListManipulation<T>
}

public sealed interface BasicListManipulation<out E> : NormalListManipulation<E> {
    override fun normalize(): List<NormalListManipulation<E>> = listOf(this)
    abstract override fun <T> map(transform: (E) -> T): NormalListManipulation<T>
}


/**
 * Meta change is a change, that is not bound to any specific element values.
 *
 * @see PermutationListManipulation
 * @see NonPermutationMetaListManipulation
 */
public sealed class MetaListManipulation : ListManipulation<Nothing> {

    override fun applyTo(target: MutableList<in Nothing>) {
        applyMetaChange(target)
    }

    public abstract fun <E> applyMetaChange(target: MutableList<E>)


    override fun <T> map(transform: (Nothing) -> T): ListManipulation<Nothing> = this
}

public sealed class NormalMetaListManipulation : MetaListManipulation(), NormalListManipulation<Nothing> {
    override fun <T> map(transform: (Nothing) -> T): NormalListManipulation<Nothing> = this
}

/**
 * Non-permutation meta change is a [MetaListManipulation] which cannot be expressed as permutation.
 *
 * @see RemoveAt
 * @see Duplicate
 * @see Clear
 */
public abstract class NonPermutationMetaListManipulation : MetaListManipulation()

public abstract class PermutationListManipulation : MetaListManipulation() {
    public abstract fun toTranspositions(): Iterable<Swap>

    override fun <E> applyMetaChange(target: MutableList<E>) {
        this.toTranspositions().forEach { it.applyTo(target) }
    }
}

public sealed class NormalPermutationListManipulation : PermutationListManipulation(), NormalListManipulation<Nothing> {
    override fun <T> map(transform: (Nothing) -> T): NormalListManipulation<Nothing> = this
}

public abstract class NonMetaListManipulation<out E> : ListManipulation<E> {
    abstract override fun <T> map(transform: (E) -> T): NonMetaListManipulation<T>
}

public data class Add<out E>(
    val index: Int,
    val element: E
) : NonMetaListManipulation<E>(), BasicListManipulation<E> {
    @Suppress("UNCHECKED_CAST")
    public open override fun <@LowerBound("E") T> toBasic(list: List<T>): List<BasicListManipulation<T>> =
        listOf(this as BasicListManipulation<T>)

    override fun applyTo(target: MutableList<in E>) {
        target.add(index, element)
    }

    override fun <T> map(transform: (E) -> T): Add<T> = Add(index, transform(element))
}

public data class AddAll<out E>(
    val index: Int,
    val elements: List<E>
) : NonMetaListManipulation<E>(), NormalListManipulation<E> {
    @Suppress("UNCHECKED_CAST")
    override fun <@LowerBound("E") T> toBasic(list: List<T>): List<BasicListManipulation<T>> =
        elements.withIndex().map { Add(index + it.index, it.value) as BasicListManipulation<T> }

    override fun applyTo(target: MutableList<in E>) {
        target.addAll(index, elements)
    }

    override fun <T> map(transform: (E) -> T): AddAll<T> = AddAll(index, elements.map(transform))
}

public data class Remove<out E>(
    val element: E
) : NonMetaListManipulation<E>(), NormalListManipulation<E> {
    @Suppress("UNCHECKED_CAST")
    public override fun <@LowerBound("E") T> toBasic(list: List<T>): List<BasicListManipulation<T>> {
        val index = (list as List<E>).indexOf(element)
        return if(index >= 0) {
            listOf(RemoveAt(index))
        } else {
            emptyList()
        }
    }

    override fun applyTo(target: MutableList<in E>) {
        target.remove(element)
    }

    override fun <T> map(transform: (E) -> T): Remove<T> = Remove(transform(element))
}

public data class RemoveAt(
    val index: Int
) : NormalMetaListManipulation(), BasicListManipulation<Nothing> {
    public override fun <@LowerBound("E") T> toBasic(list: List<T>): List<BasicListManipulation<T>> = listOf(this)

    override fun <E> applyMetaChange(target: MutableList<E>) {
        target.removeAt(index)
    }
}

public data class RemoveRange(
    val range: IntRange
) : NormalMetaListManipulation() {
    public override fun <@LowerBound("E") T> toBasic(list: List<T>): List<BasicListManipulation<T>> =
        range.reversed().map { RemoveAt(it) }

    override fun <E> applyMetaChange(target: MutableList<E>) {
        target.subList(range.first, range.last + 1).clear()
    }
}

public data class Set<out E>(
    val index: Int,
    val newElement: E
) : NonMetaListManipulation<E>(), NormalListManipulation<E> {
    public override fun <@LowerBound("E") T> toBasic(list: List<T>): List<BasicListManipulation<T>> = listOf(
        RemoveAt(index) as BasicListManipulation<T>,
        Add(index, newElement) as BasicListManipulation<T>
    )

    override fun applyTo(target: MutableList<in E>) {
        target[index] = newElement
    }

    override fun <T> map(transform: (E) -> T): Set<T> =
        Set(index, transform(newElement))
}

public data class Swap(
    val aIndex: Int,
    val bIndex: Int
) : NormalPermutationListManipulation() {
    public override fun <@LowerBound("E") T> toBasic(list: List<T>): List<BasicListManipulation<T>> = listOf(
        RemoveAt(aIndex),
        Add(aIndex, list[bIndex]),
        RemoveAt(bIndex),
        Add(bIndex, list[aIndex])
    )

    override fun toTranspositions(): List<Swap> = listOf(this)

    override fun <T> applyMetaChange(target: MutableList<T>) {
        val element = target[aIndex]
        target[aIndex] = target[bIndex]
        target[bIndex] = element
    }
}

public data class SwapRanges(
    val aRange: IntRange,
    val bRange: IntRange // TODO: Validate ranges do not intersect
) : NormalPermutationListManipulation() {
    override fun <@LowerBound("E") T> toBasic(list: List<T>): List<BasicListManipulation<T>> {
        val (firstRange, lastRange) = (aRange to bRange).sort { a, b -> a.first.compareTo(b.first) }
        return RemoveRange(lastRange).toBasic(list) +
                RemoveRange(firstRange).toBasic(list) +
                DuplicateRange(lastRange, firstRange.first).toBasic(list) +
                DuplicateRange(firstRange, lastRange.first + (lastRange.last - lastRange.first) - (firstRange.last - firstRange.first)).toBasic(list)
    }

    override fun toTranspositions(): List<Swap> = TODO()

    override fun <T> applyMetaChange(target: MutableList<T>) {
        target.swapRanges(aRange, bRange)
    }
}

public data class Move(
    val from: Int,
    val to: Int
) : NormalPermutationListManipulation() {
    override fun <@LowerBound("E") T> toBasic(list: List<T>): List<BasicListManipulation<T>> = listOf(
        RemoveAt(from) as BasicListManipulation<T>,
        Add(to, list[from]) as BasicListManipulation<T>
    )

    override fun toTranspositions(): Iterable<Swap> {
        val step = if (to > from) 1 else -1
        return IntProgression.fromClosedRange(from, to - step, step).map { Swap(it, it + step) }
    }

    override fun <T> applyMetaChange(target: MutableList<T>) {
        val element = target.removeAt(from)
        target.add(to, element)
    }
}

public data class MoveRange(
    val from: IntRange,
    val to: Int
) : NormalPermutationListManipulation() {
    override fun <@LowerBound("E") T> toBasic(list: List<T>): List<BasicListManipulation<T>> =
        RemoveRange(from).toBasic(list) + DuplicateRange(from, to).toBasic(list)

    override fun toTranspositions(): Iterable<Swap> {
        val step = if (to > from.start) 1 else -1
        return from.flatMap { itFrom ->
            val itTo = to + itFrom - from.start
            IntProgression.fromClosedRange(itFrom, itTo - step, step).map { Swap(it, it + step) }
        }
    }

    override fun <T> applyMetaChange(target: MutableList<T>) {
        val sourceRegionView = target.subList(from.start, from.endInclusive + 1)
        val movedRegionCopy = ArrayList(sourceRegionView)
        sourceRegionView.clear()
        target.subList(to, to).addAll(movedRegionCopy)
    }
}

public data class ReplaceAll<out E>(
    val elements: List<E>
) : NonMetaListManipulation<E>(), NormalListManipulation<E> {
    override fun <@LowerBound("E") T> toBasic(list: List<T>): List<BasicListManipulation<T>> =
        Clear.toBasic(list) + AddAll(0, elements).toBasic(list)

    override fun applyTo(target: MutableList<in E>) {
        target.clear()
        target.addAll(elements)
    }

    override fun <T> map(transform: (E) -> T): ReplaceAll<T> = ReplaceAll(elements.map(transform))
}

public data class Duplicate(
    val from: Int,
    val to: Int
) : NormalMetaListManipulation() {
    override fun <@LowerBound("E") T> toBasic(list: List<T>): List<BasicListManipulation<T>> =
        listOf(Add(to, list[from]))

    override fun <E> applyMetaChange(target: MutableList<E>) {
        val element = target[from]
        target.add(to, element)
    }
}

public data class DuplicateRange(
    val from: IntRange,
    val to: Int
) : NormalMetaListManipulation() {
    override fun <@LowerBound("E") T> toBasic(list: List<T>): List<BasicListManipulation<T>> =
        from.map {
            Add(to + it - from.first, list[it])
        }

    override fun <E> applyMetaChange(target: MutableList<E>) {
        val sourceRegionView = target.subList(from.start, from.endInclusive + 1)
        val movedRegionCopy = ArrayList(sourceRegionView)
        target.subList(to, to).addAll(movedRegionCopy)
    }
}

public object Clear : NormalMetaListManipulation() {
    override fun <@LowerBound("E") T> toBasic(list: List<T>): List<BasicListManipulation<T>> =
        list.map { RemoveAt(0) }

    override fun <E> applyMetaChange(target: MutableList<E>) {
        target.clear()
    }
}

private fun <T> Pair<T, T>.sort(comparator: Comparator<T>): Pair<T, T> {
    val compare = comparator.compare(first, second)
    return if (compare <= 0) {
        this
    } else {
        this.swap()
    }
}

private fun <T> Pair<T, T>.swap(): Pair<T, T> = Pair(second, first)


private fun <T> MutableList<T>.swapRanges(aRange: IntRange, bRange: IntRange) {
    val (firstRange, lastRange) = (aRange to bRange).sort { a, b -> a.first.compareTo(b.first) }
    val firstRegionView = subList(firstRange.first, firstRange.last + 1)
    val firstRegionCopy = ArrayList(firstRegionView)
    val secondRegionView = subList(lastRange.first, lastRange.last + 1)
    val secondRegionCopy = ArrayList(secondRegionView)
    firstRegionView.clear()
    secondRegionView.clear()
    firstRegionView.addAll(secondRegionCopy)
    secondRegionView.addAll(firstRegionCopy)
}
//TODO: Validate all ranges do not contain negative indexes
