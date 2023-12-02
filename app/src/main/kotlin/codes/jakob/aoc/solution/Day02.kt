package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.associateByIndex
import codes.jakob.aoc.shared.multiply
import codes.jakob.aoc.shared.splitMultiline

object Day02 : Solution() {
    override fun solvePart1(input: String): Any {
        val cubeAmountConstraints: Map<CubeType, Int> = mapOf(
            CubeType.RED to 12,
            CubeType.GREEN to 13,
            CubeType.BLUE to 14,
        )

        val games: Map<Int, List<Map<CubeType, Int>>> = parseGames(input)

        val possibleGames: Map<Int, List<Map<CubeType, Int>>> = games
            .filterValues { game: List<Map<CubeType, Int>> ->
                val maximumSeenCubes: Map<CubeType, Int> = countMaximumCubeAmount(game)
                maximumSeenCubes.all { (cubeType: CubeType, maximumSeen: Int) ->
                    maximumSeen <= cubeAmountConstraints.getOrDefault(cubeType, 0)
                }
            }

        return possibleGames.keys.sum()
    }

    override fun solvePart2(input: String): Any {
        val games: Map<Int, List<Map<CubeType, Int>>> = parseGames(input)

        val minimumRequiredCubesPerGame: Map<Int, Map<CubeType, Int>> = games
            .mapValues { (_, game: List<Map<CubeType, Int>>) -> countMaximumCubeAmount(game).toMap() }

        return minimumRequiredCubesPerGame
            .map { (_, minimumRequiredCubes: Map<CubeType, Int>) -> minimumRequiredCubes.values.multiply() }
            .sum()
    }

    private fun parseGames(input: String): Map<Int, List<Map<CubeType, Int>>> {
        fun parseGame(line: String): List<Map<CubeType, Int>> {
            fun parseCubeAmount(cubeString: String): Pair<CubeType, Int> {
                val (amount, type) = cubeString.split(" ")
                return CubeType.fromSign(type) to amount.toInt()
            }

            return line
                .substringAfter(": ")
                .split("; ")
                .map { reveal -> reveal.split(", ") }
                .map { reveal -> reveal.map { parseCubeAmount(it) } }
                .map { reveal -> reveal.toMap() }
        }

        return input
            .splitMultiline()
            .map { parseGame(it) }
            .associateByIndex()
            .mapKeys { (index, _) -> index + 1 }
    }

    private fun countMaximumCubeAmount(game: List<Map<CubeType, Int>>): Map<CubeType, Int> {
        val maximumSeenCubes: MutableMap<CubeType, Int> = mutableMapOf()
        game.forEach { reveal: Map<CubeType, Int> ->
            reveal.forEach { (cubeType: CubeType, amount: Int) ->
                maximumSeenCubes[cubeType] = maxOf(maximumSeenCubes.getOrDefault(cubeType, 0), amount)
            }
        }
        return maximumSeenCubes
    }

    enum class CubeType {
        RED,
        GREEN,
        BLUE;

        companion object {
            fun fromSign(sign: String): CubeType {
                return when (sign) {
                    "red" -> RED
                    "green" -> GREEN
                    "blue" -> BLUE
                    else -> error("Unknown cube type: $sign")
                }
            }
        }
    }
}

fun main() {
    Day02.solve()
}
