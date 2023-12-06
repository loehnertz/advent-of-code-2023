package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.middle
import codes.jakob.aoc.shared.multiply
import codes.jakob.aoc.shared.splitByLines

object Day06 : Solution() {
    override fun solvePart1(input: String): Any {
        /**
         * Parses the race records from the [input] string.
         * The [input] string is expected to be in the following format:
         * ```
         * Time:      7  15   30
         * Distance:  9  40  200
         * ```
         * Each whitespace between the numbers is treated as a delimiter.
         */
        fun parseRaceRecords(input: String): List<RaceRecord> {
            val numberPattern = Regex("\\d+")
            val (timeLine, distanceLine) = input.splitByLines()
            val times: List<Long> = numberPattern.findAll(timeLine).map { it.value.toLong() }.toList()
            val distances: List<Long> = numberPattern.findAll(distanceLine).map { it.value.toLong() }.toList()
            return times.zip(distances) { time, distance -> RaceRecord(time, distance) }
        }

        return parseRaceRecords(input).map { findPossibleButtonHoldTimes(it) }.multiply()
    }

    override fun solvePart2(input: String): Any {
        /**
         * Parses the race records from the [input] string.
         * The [input] string is expected to be in the following format:
         * ```
         * Time:      7  15   30
         * Distance:  9  40  200
         * ```
         * The whitespace between the numbers is ignored, and the numbers are treated as a single one.
         */
        fun parseRaceRecord(input: String): RaceRecord {
            val numberPattern = Regex("\\d+")
            val (timeLine, distanceLine) = input.splitByLines()
            val time: Long = numberPattern.findAll(timeLine).map { it.value }.joinToString("").toLong()
            val distance: Long = numberPattern.findAll(distanceLine).map { it.value }.joinToString("").toLong()
            return RaceRecord(time, distance)
        }

        return findPossibleButtonHoldTimes(parseRaceRecord(input))
    }

    /**
     * Finds the number of feasible button hold times for a given [record] to beat.
     */
    private fun findPossibleButtonHoldTimes(record: RaceRecord): Long {
        /**
         * Calculates the distance the boat travels when the button is held for [buttonHoldTime] milliseconds.
         */
        fun calculateDistance(raceRecord: RaceRecord, buttonHoldTime: Long): Long {
            val timeLeft: Long = raceRecord.time - buttonHoldTime
            return buttonHoldTime * timeLeft
        }

        val toBeEvaluatedButtonHoldTimes: ArrayDeque<Long> = ArrayDeque<Long>().also {
            it.add((0..record.time).middle())
        }
        val evaluatedButtonHoldTimes: MutableSet<Long> = mutableSetOf()
        var feasibleButtonHoldTimes: Long = 0
        while (toBeEvaluatedButtonHoldTimes.isNotEmpty()) {
            val buttonHoldTime: Long = toBeEvaluatedButtonHoldTimes.removeFirst().also {
                evaluatedButtonHoldTimes.add(it)
            }
            val distance: Long = calculateDistance(record, buttonHoldTime)
            if (distance > record.distance) {
                feasibleButtonHoldTimes++

                val next: Long = buttonHoldTime + 1
                val previous: Long = buttonHoldTime - 1
                if (next !in evaluatedButtonHoldTimes) toBeEvaluatedButtonHoldTimes.add(next)
                if (previous !in evaluatedButtonHoldTimes) toBeEvaluatedButtonHoldTimes.add(previous)
            }
        }
        return feasibleButtonHoldTimes
    }

    private data class RaceRecord(
        val time: Long,
        val distance: Long,
    )
}

fun main() = Day06.solve()
