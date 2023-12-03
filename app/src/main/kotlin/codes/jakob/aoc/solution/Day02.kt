package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.associateByIndex
import codes.jakob.aoc.shared.multiply
import codes.jakob.aoc.shared.splitByLines

object Day02 : Solution() {
    override fun solvePart1(input: String): Any {
        // The amount of cubes of each type is constrained by the following rules:
        val cubeAmountConstraints: Map<CubeType, Int> = mapOf(
            CubeType.RED to 12,
            CubeType.GREEN to 13,
            CubeType.BLUE to 14,
        )

        // The games are represented as a map of game index to a list of reveals.
        val games: Map<Int, List<Map<CubeType, Int>>> = parseGames(input)

        // We filter out all games that violate the cube amount constraints.
        val possibleGames: Map<Int, List<Map<CubeType, Int>>> = games
            .filterValues { game: List<Map<CubeType, Int>> ->
                val maximumSeenCubes: Map<CubeType, Int> = countMaximumCubeAmount(game)
                maximumSeenCubes.all { (cubeType: CubeType, maximumSeen: Int) ->
                    maximumSeen <= cubeAmountConstraints.getOrDefault(cubeType, 0)
                }
            }

        // We sum up the game indices of the possible games.
        return possibleGames.keys.sum()
    }

    override fun solvePart2(input: String): Any {
        // The games are represented as a map of game index to a list of reveals.
        val games: Map<Int, List<Map<CubeType, Int>>> = parseGames(input)

        // We calculate the minimum required cubes per game.
        val minimumRequiredCubesPerGame: Map<Int, Map<CubeType, Int>> = games
            .mapValues { (_, game: List<Map<CubeType, Int>>) -> countMaximumCubeAmount(game).toMap() }

        // We sum up the minimum required cubes per game.
        return minimumRequiredCubesPerGame
            .map { (_, minimumRequiredCubes: Map<CubeType, Int>) -> minimumRequiredCubes.values.multiply() }
            .sum()
    }

    /**
     * Parses the input into a map of game index to a list of reveals.
     */
    private fun parseGames(input: String): Map<Int, List<Map<CubeType, Int>>> {
        /**
         * Parses a single game.
         * A game is represented as a list of reveals. A reveal is represented as a map of cube type to amount.
         * Example: "red 1, green 2, blue 3" -> {RED=1, GREEN=2, BLUE=3}
         */
        fun parseGame(line: String): List<Map<CubeType, Int>> {
            /**
             * Parses a single reveal.
             * A reveal is represented as a map of cube type to amount.
             */
            fun parseCubeAmount(cubeString: String): Pair<CubeType, Int> {
                val (amount, type) = cubeString.split(" ")
                return CubeType.fromSign(type) to amount.toInt()
            }

            // We split the line into reveals, then we split each reveal into cube types and amounts.
            return line
                .substringAfter(": ")
                .split("; ")
                .map { reveal -> reveal.split(", ") }
                .map { reveal -> reveal.map { parseCubeAmount(it) } }
                .map { reveal -> reveal.toMap() }
        }

        // We split the input into games, then we parse each game.
        // We then associate each game with its index (as it's given with its index in the input).
        return input
            .splitByLines()
            .map { parseGame(it) }
            .associateByIndex()
            .mapKeys { (index, _) -> index + 1 }
    }

    /**
     * Counts the maximum amount of cubes of each type that are seen in a game.
     */
    private fun countMaximumCubeAmount(game: List<Map<CubeType, Int>>): Map<CubeType, Int> {
        val maximumSeenCubes: MutableMap<CubeType, Int> = mutableMapOf()
        game.forEach { reveal: Map<CubeType, Int> ->
            reveal.forEach { (cubeType: CubeType, amount: Int) ->
                maximumSeenCubes[cubeType] = maxOf(maximumSeenCubes.getOrDefault(cubeType, 0), amount)
            }
        }
        return maximumSeenCubes
    }

    /**
     * Represents the type of cube.
     * The type of cube is represented as a sign (e.g., "red", "green", "blue").
     */
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
