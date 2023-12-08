package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.cyclicIterator
import codes.jakob.aoc.shared.lowestCommonMultiple
import codes.jakob.aoc.shared.splitByCharacter

object Day08 : Solution() {
    private val mapPattern = Regex("([A-Z]{3})\\s=\\s\\(([A-Z]{3}),\\s([A-Z]{3})\\)")

    override fun solvePart1(input: String): Any {
        val (instructions: List<Direction>, map: Map<Node, Junction>) = parseInput(input)

        val startNode = Node("AAA")
        val endNode = Node("ZZZ")

        val nodeWalker = NodeWalker(map, startNode) { it == endNode }
        return nodeWalker.countSteps(instructions)
    }

    override fun solvePart2(input: String): Any {
        val (instructions: List<Direction>, map: Map<Node, Junction>) = parseInput(input)

        fun Node.isStart(): Boolean = lastSymbol == 'A'
        fun Node.isEnd(): Boolean = lastSymbol == 'Z'

        val startNodes: Set<Node> = map.keys.filter { it.isStart() }.toSet()

        val requiredStepsByNode: Map<Node, Int> = startNodes.associateWith { node ->
            val nodeWalker = NodeWalker(map, node) { it.isEnd() }
            nodeWalker.countSteps(instructions)
        }
        return requiredStepsByNode.values.lowestCommonMultiple()
    }

    private fun parseInput(input: String): Pair<List<Direction>, Map<Node, Junction>> {
        val (instructionString: String, mapString: String) = input.split("\n\n")
        val instructions: List<Direction> = instructionString.splitByCharacter().map { Direction.fromSymbol(it) }
        val map: Map<Node, Junction> = mapPattern.findAll(mapString).map { matchResult ->
            val (source: String, leftTarget: String, rightTarget: String) = matchResult.destructured
            Node(source) to Junction(Node(leftTarget), Node(rightTarget))
        }.toMap()
        return instructions to map
    }

    private class NodeWalker(
        private val map: Map<Node, Junction>,
        startNode: Node,
        private val isEndNode: (Node) -> Boolean,
    ) {
        private var currentNode: Node = startNode

        fun countSteps(instructions: List<Direction>): Int = step(currentNode, instructions.cyclicIterator(), 0)

        private tailrec fun step(currentNode: Node, remainingDirections: Iterator<Direction>, steps: Int): Int {
            return if (!isEndNode(currentNode)) {
                val junction: Junction = map[currentNode] ?: error("The node '$currentNode' is not in the map.")
                val nextDirection: Direction = remainingDirections.next()
                val nextNode: Node = junction.nextNode(nextDirection)
                step(nextNode, remainingDirections, steps + 1)
            } else steps
        }
    }

    private enum class Direction {
        LEFT,
        RIGHT;

        companion object {
            fun fromSymbol(symbol: Char): Direction = when (symbol) {
                'L' -> LEFT
                'R' -> RIGHT
                else -> error("The symbol '$symbol' is not a valid direction.")
            }
        }
    }

    @JvmInline
    private value class Node(
        val value: String,
    ) {
        init {
            require(value.length == 3) { "The value '$value' is not a valid node." }
        }

        val firstSymbol: Char
            get() = value.first()
        val lastSymbol: Char
            get() = value.last()
    }

    private data class Junction(
        val left: Node,
        val right: Node,
    ) {
        fun nextNode(direction: Direction): Node = when (direction) {
            Direction.LEFT -> left
            Direction.RIGHT -> right
        }
    }
}

fun main() = Day08.solve()
