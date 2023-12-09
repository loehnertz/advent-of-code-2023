package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.splitByLines
import codes.jakob.aoc.shared.splitBySpace

object Day09 : Solution() {
    override fun solvePart1(input: String): Any {
        return parseHistories(input)
            .map { HistoryTree.resolveFromHistory(it) }
            .map { it.resolveNextValues() }
            .sumOf { it.firstLastValue() }
    }

    override fun solvePart2(input: String): Any {
        return parseHistories(input)
            .map { HistoryTree.resolveFromHistory(it) }
            .map { it.resolvePreviousValues() }
            .sumOf { it.firstFirstValue() }
    }

    private fun parseHistories(input: String): List<History> {
        return input
            .splitByLines()
            .map { line -> line.splitBySpace() }
            .map { numbers -> History(numbers.map { it.toLong() }) }
    }

    @JvmInline
    private value class History(val values: List<Long>) {
        fun addLast(number: Long): History {
            return History(values + listOf(number))
        }

        fun addFirst(number: Long): History {
            return History(listOf(number) + values)
        }

        fun combine(): History {
            return History(values.zipWithNext { a, b -> b - a })
        }
    }

    @JvmInline
    private value class HistoryTree(val tree: List<History>) {
        init {
            require(tree.isNotEmpty()) { "Tree must not be empty" }
            require(tree.last().allZeroes()) { "Last element of tree must be all zeroes" }
        }

        fun resolveNextValues(): HistoryTree {
            return tree.foldRightIndexed(listOf<History>()) { index, currentHistory, newTree ->
                if (index == tree.lastIndex) {
                    val newHistory: History = currentHistory.addLast(0)
                    listOf(newHistory)
                } else {
                    val previousHistory: History = newTree.first()
                    val newLastNumberCurrent: Long = currentHistory.values.last() + previousHistory.values.last()
                    val newHistory: History = currentHistory.addLast(newLastNumberCurrent)
                    listOf(newHistory) + newTree
                }
            }.let { HistoryTree(it) }
        }

        fun resolvePreviousValues(): HistoryTree {
            return tree.foldRightIndexed(listOf<History>()) { index, currentHistory, newTree ->
                if (index == tree.lastIndex) {
                    val newHistory: History = currentHistory.addFirst(0)
                    listOf(newHistory)
                } else {
                    val previousHistory: History = newTree.first()
                    val newFirstNumberCurrent: Long = currentHistory.values.first() - previousHistory.values.first()
                    val newHistory: History = currentHistory.addFirst(newFirstNumberCurrent)
                    listOf(newHistory) + newTree
                }
            }.let { HistoryTree(it) }
        }

        companion object {
            fun resolveFromHistory(history: History): HistoryTree {
                tailrec fun resolve(history: History, previous: List<History>): HistoryTree {
                    if (history.allZeroes()) return HistoryTree(previous)
                    val newHistory: History = history.combine()
                    return resolve(newHistory, previous + newHistory)
                }

                return resolve(history, listOf(history))
            }
        }
    }

    private fun History.allZeroes(): Boolean = values.all { it == 0L }

    private fun HistoryTree.firstLastValue(): Long = tree.first().values.last()

    private fun HistoryTree.firstFirstValue(): Long = tree.first().values.first()
}

fun main() = Day09.solve()
