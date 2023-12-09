package codes.jakob.aoc.shared

import java.util.*


fun String.splitByLines(): List<String> = split("\n")

fun String.splitByCharacter(): List<Char> = split("").filterNot { it.isBlank() }.map { it.toSingleChar() }

fun String.splitBySpace(): List<String> = split(" ")

fun Int.isEven(): Boolean = this % 2 == 0

fun Int.isOdd(): Boolean = !isEven()

fun <E> List<E>.middleOrNull(): E? {
    return if (this.count().isOdd()) this[this.count() / 2] else null
}

fun <T> Iterable<T>.productOf(selector: (T) -> Int): Int {
    var product = 1
    for (element in this) product *= selector(element)
    return product
}

/**
 * Calculates the [triangular number](https://en.wikipedia.org/wiki/Triangular_number) of the given number.
 */
fun Long.triangular(): Long = ((this * (this + 1)) / 2)

fun CharSequence.toSingleChar(): Char {
    require(this.count() == 1) { "The given CharSequence has more than one element" }
    return this.first()
}

operator fun <T> T.plus(collection: Collection<T>): List<T> {
    val result = ArrayList<T>(collection.size + 1)
    result.add(this)
    result.addAll(collection)
    return result
}

fun <T, K> Collection<T>.countBy(keySelector: (T) -> K): Map<K, Int> {
    return this.groupingBy(keySelector).eachCount()
}

/**
 * Returns any given [Map] with its keys and values reversed (i.e., the keys becoming the values and vice versa).
 * Note in case of duplicate values, they will be overridden in the key-set unpredictably.
 */
fun <K, V> Map<K, V>.reversed(): Map<V, K> {
    return HashMap<V, K>(this.count()).also { reversedMap: HashMap<V, K> ->
        this.entries.forEach { reversedMap[it.value] = it.key }
    }
}

fun <E> Stack<E>.peekOrNull(): E? {
    return if (this.isNotEmpty()) this.peek() else null
}

fun <E> List<E>.associateByIndex(): Map<Int, E> {
    return this.mapIndexed { index, element -> index to element }.toMap()
}

private val NUMBER_PATTERN = Regex("\\d+")
fun String.isNumber(): Boolean = NUMBER_PATTERN.matches(this)

fun <K, V, NK> Map<K, V>.mapKeysMergingValues(
    transformKey: (K, V) -> NK,
    mergeValues: (V, V) -> V,
): Map<NK, V> {
    return this
        .asSequence()
        .map { (key, value) -> transformKey(key, value) to value }
        .groupBy({ it.first }, { it.second })
        .mapValues { (_, values) -> values.reduce(mergeValues) }
}

inline fun <T, R> Pair<T, T>.map(block: (T) -> R): Pair<R, R> {
    return this.let { (first: T, second: T) ->
        block(first) to block(second)
    }
}

inline fun <A, B, C, D> Pair<A, B>.map(blockA: (A) -> C, blockB: (B) -> D): Pair<C, D> {
    return this.let { (first: A, second: B) ->
        blockA(first) to blockB(second)
    }
}

fun <E> List<E>.splitInHalf(): Pair<List<E>, List<E>> {
    return this.subList(0, this.size / 2) to this.subList(this.size / 2, this.size)
}

fun List<Int>.binaryToDecimal(): Int {
    require(this.all { it == 0 || it == 1 }) { "Expected bit string, but received $this" }
    return Integer.parseInt(this.joinToString(""), 2)
}

fun Int.bitFlip(): Int {
    require(this == 0 || this == 1) { "Expected bit, but received $this" }
    return this.xor(1)
}

fun String.toBitString(): List<Int> {
    val bits: List<String> = split("").filter { it.isNotBlank() }
    require(bits.all { it == "0" || it == "1" }) { "Expected bit string, but received $this" }
    return bits.map { it.toInt() }
}

/**
 * [Transposes](https://en.wikipedia.org/wiki/Transpose) the given list of nested lists (a matrix, in essence).
 *
 * This function is adapted from this [post](https://stackoverflow.com/a/66401340).
 */
fun <T> List<List<T>>.transpose(): List<List<T>> {
    val result: MutableList<MutableList<T>> = (this.first().indices).map { mutableListOf<T>() }.toMutableList()
    this.forEach { columns -> result.zip(columns).forEach { (rows, cell) -> rows.add(cell) } }
    return result
}

infix fun <T : Comparable<T>> ClosedRange<T>.fullyContains(other: ClosedRange<T>): Boolean {
    return this.start in other && this.endInclusive in other
}

infix fun <T : Comparable<T>> ClosedRange<T>.overlaps(other: ClosedRange<T>): Boolean {
    return this.start in other || this.endInclusive in other
}

fun <E> List<E>.toPair(): Pair<E, E> {
    require(this.size == 2) { "The given list has to contain exactly two elements, instead found ${this.size}" }
    return this[0] to this[1]
}

fun String.parseRange(delimiter: Char = '-'): IntRange {
    val (from: Int, to: Int) = this.split(delimiter).map { it.toInt() }
    return from..to
}

@Synchronized
fun <K, V> MutableMap<K, V>.getOrCompute(key: K, valueFunction: () -> V): V {
    return this[key] ?: valueFunction().also { this[key] = it }
}

fun <T, R> Collection<T>.allUnique(block: (T) -> R): Boolean {
    val mapped: List<R> = this.map(block)
    return mapped.size == mapped.toSet().size
}

fun <E> Collection<E>.cartesianProduct(): List<Pair<E, E>> {
    return this.flatMap { lhs: E -> this.map { rhs: E -> lhs to rhs } }
}

fun Iterable<Int>.multiply(): Int = fold(1) { a, i -> a * i }

fun Iterable<Long>.multiply(): Long = fold(1L) { a, l -> a * l }

fun Char.parseInt(): Int = toString().toInt()

fun <T> String.parseMatrix(block: (Char) -> T): List<List<T>> {
    return this.splitByLines().map { it.splitByCharacter().map(block) }
}

fun <T> String.parseGrid(block: (Char) -> T): Grid<T> {
    return Grid.fromMatrix(this.parseMatrix(block))
}

fun <T, R> memoize(block: (T) -> R): (T) -> R {
    val cache: MutableMap<T, R> = mutableMapOf()
    return { input: T ->
        cache.getOrPut(input) { block(input) }
    }
}

fun IntProgression.middle(): Int = (first + last) / 2

fun LongProgression.middle(): Long = (first + last) / 2

fun <E> Collection<E>.containsTimes(e: E, times: Int): Boolean = count { it == e } == times

fun <T> Iterable<T>.notAll(block: (T) -> Boolean): Boolean = !all(block)

fun Collection<Number>.lowestCommonMultiple(): Long {
    /**
     * Calculates the "lowest common multiple" of two numbers.
     */
    fun lcm(a: Long, b: Long): Long {
        /**
         * Calculates the "greatest common divisor" of two numbers.
         */
        fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

        return a / gcd(a, b) * b
    }

    return if (this.isNotEmpty() && !this.contains(0L)) {
        this.distinct().map { it.toLong() }.reduce { acc, num -> lcm(acc, num) }
    } else 0L // LCM is not defined for empty sets or sets containing 0
}
