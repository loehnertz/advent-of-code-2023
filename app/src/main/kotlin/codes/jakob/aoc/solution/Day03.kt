package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.*
import codes.jakob.aoc.shared.Grid.Companion.fromRawList

object Day03 : Solution() {
    private val symbolPattern = Regex("[^.\\d]")
    private val gearPattern = Regex("\\*")

    override fun solvePart1(input: String): Any {
        return buildGrid(input).cells
            .asSequence()
            // Filter out all cells that are not a digit
            .filter { cell -> cell.value.isDigit() }
            // Filter out all cells that are not adjacent to a symbol
            .filter { cell ->
                cell.getAdjacent(diagonally = true).any { adjacent ->
                    symbolPattern.matches(adjacent.value.toString())
                }
            }
            // Map each cell to the starting cell of the number it belongs to (e.g., 987 -> 9)
            .map { cell -> findStartingCell(cell) }
            // Filter out all duplicate starting cells
            .distinct()
            // Build the numbers from the starting cells
            .map { startingCell -> buildNumberFromStartingCell(startingCell) }
            // Convert the numbers (lists of Cell<Char>) to integers by joining them
            .map { numberCells ->
                numberCells.joinToString("") { cell -> cell.value.toString() }.toInt()
            }
            // Sum up the numbers
            .sum()
    }

    override fun solvePart2(input: String): Any {
        return buildGrid(input).cells
            // Filter out all cells that are not a digit
            .filter { cell -> cell.value.isDigit() }
            // Associate each cell with all adjacent cells that are gears
            .associateWith { cell ->
                cell.getAdjacent(diagonally = true).filter { adjacent ->
                    gearPattern.matches(adjacent.value.toString())
                }.toSet()
            }
            // Associate each cell with the starting cell of the number it belongs to (e.g., 987 -> 9)
            // As the result is a hash map, the number cells are grouped by their starting cell
            .mapKeysMergingValues(
                { cell, _ -> findStartingCell(cell) },
                { accumulator, adjacentsGears ->
                    setOf(*accumulator.toTypedArray(), *adjacentsGears.toTypedArray())
                }
            )
            // Only retain the cells that have an adjacent gear
            .filterValues { gears -> gears.isNotEmpty() }
            // Only retain the first gear (as there cannot be more than one gear adjacent to a part number)
            .mapValues { (_, gears) -> gears.first() }
            // Group the part numbers by their adjacent gear cell
            .entries
            .groupBy(
                { (_, gears) -> gears },
                { (partNumber, _) -> partNumber }
            )
            // Only retain the gears that are adjacent to exactly two part numbers
            .filterValues { partNumbers -> partNumbers.size == 2 }
            // Build the part numbers from the starting cells
            .mapValues { (_, startingCells) ->
                startingCells.map { buildNumberFromStartingCell(it) }
            }
            // Convert the part numbers (lists of Cell<Char>) to integers by joining them
            .mapValues { (_, partNumberDigitMatches) ->
                partNumberDigitMatches.map { partNumberDigits ->
                    partNumberDigits.joinToString("") { cell -> cell.value.toString() }.toInt()
                }
            }
            // Multiply the part numbers and sum them up
            .mapValues { (_, partNumbers) -> partNumbers.multiply() }
            .values
            .sum()
    }

    private fun buildGrid(input: String): Grid<Char> {
        val matrix: List<List<Char>> = input.splitMultiline().map { row -> row.splitByCharacter() }
        return Grid(fromRawList(matrix))
    }

    /**
     * Finds the starting cell of a number by going west until the next cell is not a digit anymore.
     */
    private fun findStartingCell(cell: Grid.Cell<Char>): Grid.Cell<Char> {
        tailrec fun findStartingCell(currentCell: Grid.Cell<Char>): Grid.Cell<Char> {
            val adjacentWest: Grid.Cell<Char>? = currentCell.getAdjacent(Grid.Direction.WEST)
            return if (adjacentWest?.value?.isDigit() == true) {
                findStartingCell(adjacentWest)
            } else currentCell
        }

        return findStartingCell(cell)
    }

    /**
     * Builds a number from a starting cell by going east until the next cell is not a digit anymore.
     */
    private fun buildNumberFromStartingCell(cell: Grid.Cell<Char>): List<Grid.Cell<Char>> {
        val partNumberCells: MutableList<Grid.Cell<Char>> = mutableListOf(cell)

        tailrec fun addNextDigitCell(currentCell: Grid.Cell<Char>?) {
            if (currentCell?.value?.isDigit() == true) {
                partNumberCells.add(currentCell)
                addNextDigitCell(currentCell.getAdjacent(Grid.Direction.EAST))
            }
        }

        addNextDigitCell(cell.getAdjacent(Grid.Direction.EAST))
        return partNumberCells
    }
}

fun main() {
    Day03.solve()
}
