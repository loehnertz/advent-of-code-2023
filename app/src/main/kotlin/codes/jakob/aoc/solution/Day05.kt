package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.splitByLines

object Day05 : Solution() {
    private val mapPattern = Regex("(\\w+)-to-(\\w+)\\smap:")

    override fun solvePart1(input: String): Any {
        val splitInput: List<List<String>> = input.split("\n\n").map { it.splitByLines() }
        val seeds: List<Long> = splitInput.first().first().let { line ->
            line.substringAfter("seeds: ").split(" ").map { it.toLong() }
        }
        val instructionMaps: List<InstructionMap> = parseInstructionMaps(splitInput.drop(1))
        // Find the seed that produces the lowest location number
        return seeds.minOf { seed: Long -> instructionMaps.findLocationNumber(seed) }
    }

    override fun solvePart2(input: String): Any {
        val splitInput: List<List<String>> = input.split("\n\n").map { it.splitByLines() }
        val seedRanges: List<LongRange> = splitInput.first().first().let { line ->
            line
                .substringAfter("seeds: ")
                .split(" ")
                .map { it.toLong() }
                .chunked(2)
                .map { (startRange, length) -> startRange until (startRange + length) }
        }
        val instructionMaps: List<InstructionMap> = parseInstructionMaps(splitInput.drop(1))
        // Find the seed that produces the lowest location number for each range, by just brute-forcing it
        return seedRanges.minOf { seedRange: LongRange ->
            seedRange.fold(Long.MAX_VALUE) { currentLowest, seed ->
                val locationNumber: Long = instructionMaps.findLocationNumber(seed)
                if (locationNumber < currentLowest) locationNumber else currentLowest
            }
        }
    }

    private fun parseInstructionMaps(splitInput: List<List<String>>): List<InstructionMap> {
        return splitInput.map { map: List<String> ->
            val (source, destination) = mapPattern.find(map.first())!!.destructured
            val type: Pair<String, String> = source to destination
            val ranges: List<InstructionMap.Ranges> = map
                .drop(1)
                .map { line ->
                    line.split(" ").map { it.toLong() }
                }
                .map { (destinationRangeStart, sourceRangeStart, length) ->
                    InstructionMap.Ranges(
                        source = sourceRangeStart until (sourceRangeStart + length),
                        destination = destinationRangeStart until (destinationRangeStart + length),
                    )
                }
            InstructionMap(type, ranges)
        }
    }

    private fun List<InstructionMap>.findLocationNumber(seed: Long): Long {
        return this.fold(seed) { currentNumber, instructionMap ->
            instructionMap.getMapping(currentNumber)
        }
    }

    private data class InstructionMap(
        val type: Pair<String, String>,
        val mappings: List<Ranges>,
    ) {
        fun getMapping(source: Long): Long {
            val ranges: Ranges = mappings.find { ranges ->
                source in ranges.source
            } ?: return source
            return ranges.destination.first + (source - ranges.source.first)
        }

        data class Ranges(
            val source: LongRange,
            val destination: LongRange,
        )
    }
}

fun main() = Day05.solve()
