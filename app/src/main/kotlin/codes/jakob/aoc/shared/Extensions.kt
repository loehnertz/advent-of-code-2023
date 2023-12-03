@file:Suppress("unused")

package codes.jakob.aoc.shared

import java.util.*


fun String.splitByCharacter(): List<Char> = split("").filterNot { it.isBlank() }.map { it.toSingleChar() }

fun String.splitMultiline(): List<String> = split("\n")

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

fun <E : Number> Collection<E>.multiply(): Int {
    return this.fold(1) { acc: Int, number: E -> acc * number.toInt() }
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
