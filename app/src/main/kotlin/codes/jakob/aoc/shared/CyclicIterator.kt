package codes.jakob.aoc.shared

/**
 * An iterator that cycles through the elements of an iterable.
 */
class CyclicIterator<T>(iterable: Iterable<T>) : Iterator<T> {
    private val elements: List<T> = iterable.toList()
    private val maxIndex: Int = elements.count() - 1
    private var currentIndex: Int = 0

    /**
     * Returns `true` if the iteration has more elements.
     */
    override fun hasNext(): Boolean = true

    /**
     * Returns the next element in the iteration.
     */
    @Synchronized
    override fun next(): T {
        return elements[currentIndex].also { if (currentIndex == maxIndex) currentIndex = 0 else currentIndex++ }
    }
}

fun <T> Iterable<T>.cyclicIterator(): CyclicIterator<T> = CyclicIterator(this)
