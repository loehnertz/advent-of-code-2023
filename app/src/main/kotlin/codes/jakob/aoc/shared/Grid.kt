package codes.jakob.aoc.shared

import codes.jakob.aoc.shared.Grid.Direction.*

class Grid<T>(input: List<List<(Cell<T>) -> T>>) {
    constructor(
        coordinateValues: Map<Coordinates, (Cell<T>) -> T>,
        defaultValueConstructor: (Cell<T>) -> T,
    ) : this(fromMap(coordinateValues, defaultValueConstructor))

    val matrix: List<List<Cell<T>>> = generateMatrix(input)
    val cells: LinkedHashSet<Cell<T>> = LinkedHashSet(matrix.flatten())

    fun getAdjacent(x: Int, y: Int, diagonally: Boolean = false): List<Cell<T>> {
        return listOfNotNull(
            matrix.getOrNull(y - 1)?.getOrNull(x),
            if (diagonally) matrix.getOrNull(y - 1)?.getOrNull(x + 1) else null,
            matrix.getOrNull(y)?.getOrNull(x + 1),
            if (diagonally) matrix.getOrNull(y + 1)?.getOrNull(x + 1) else null,
            matrix.getOrNull(y + 1)?.getOrNull(x),
            if (diagonally) matrix.getOrNull(y + 1)?.getOrNull(x - 1) else null,
            matrix.getOrNull(y)?.getOrNull(x - 1),
            if (diagonally) matrix.getOrNull(y - 1)?.getOrNull(x - 1) else null,
        )
    }

    private fun generateMatrix(input: List<List<(Cell<T>) -> T>>): List<List<Cell<T>>> {
        return input.mapIndexed { y: Int, row: List<(Cell<T>) -> T> ->
            row.mapIndexed { x: Int, valueConstructor: (Cell<T>) -> T ->
                Cell(this, x, y, valueConstructor)
            }
        }
    }

    class Cell<T>(
        private val grid: Grid<T>,
        val coordinates: Coordinates,
        valueConstructor: (Cell<T>) -> T,
    ) {
        constructor(
            grid: Grid<T>,
            x: Int,
            y: Int,
            valueConstructor: (Cell<T>) -> T,
        ) : this(grid, Coordinates(x, y), valueConstructor)

        val value: T = valueConstructor(this)

        fun getAdjacent(diagonally: Boolean = false): List<Cell<T>> {
            return grid.getAdjacent(coordinates.x, coordinates.y, diagonally)
        }

        fun getAdjacent(direction: Direction): Cell<T>? {
            return when (direction) {
                NORTH -> grid.matrix.getOrNull(coordinates.y - 1)?.getOrNull(coordinates.x)
                NORTH_EAST -> grid.matrix.getOrNull(coordinates.y - 1)?.getOrNull(coordinates.x + 1)
                EAST -> grid.matrix.getOrNull(coordinates.y)?.getOrNull(coordinates.x + 1)
                SOUTH_EAST -> grid.matrix.getOrNull(coordinates.y + 1)?.getOrNull(coordinates.x + 1)
                SOUTH -> grid.matrix.getOrNull(coordinates.y + 1)?.getOrNull(coordinates.x)
                SOUTH_WEST -> grid.matrix.getOrNull(coordinates.y + 1)?.getOrNull(coordinates.x - 1)
                WEST -> grid.matrix.getOrNull(coordinates.y)?.getOrNull(coordinates.x - 1)
                NORTH_WEST -> grid.matrix.getOrNull(coordinates.y - 1)?.getOrNull(coordinates.x - 1)
            }
        }

        fun distanceTo(other: Cell<T>, diagonally: Boolean = false): Int {
            require(other in grid.cells) { "Start and end point are not in the same grid" }
            return this.coordinates.distanceTo(other.coordinates, diagonally)
        }

        override fun toString(): String {
            return "Cell(value=$value, coordinates=$coordinates)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Cell<*>

            if (grid != other.grid) return false
            if (coordinates != other.coordinates) return false

            return true
        }

        override fun hashCode(): Int {
            var result: Int = grid.hashCode()
            result = 31 * result + coordinates.hashCode()
            return result
        }
    }

    enum class Direction {
        NORTH,
        NORTH_EAST,
        EAST,
        SOUTH_EAST,
        SOUTH,
        SOUTH_WEST,
        WEST,
        NORTH_WEST;
    }

    companion object {
        fun <T> fromMap(
            coordinateValues: Map<Coordinates, (Cell<T>) -> T>,
            defaultValueConstructor: (Cell<T>) -> T,
        ): List<List<(Cell<T>) -> T>> {
            val maxX: Int = coordinateValues.keys.maxOf { it.x } + 1
            val maxY: Int = coordinateValues.keys.maxOf { it.y } + 1
            return List(maxY) { y: Int ->
                List(maxX) { x: Int ->
                    coordinateValues[Coordinates(x, y)] ?: defaultValueConstructor
                }
            }
        }

        fun <T> fromRawList(input: List<List<T>>): List<List<(Cell<T>) -> T>> {
            return input.map { row: List<T> ->
                row.map { cell: T ->
                    { _: Cell<T> -> cell }
                }
            }
        }
    }
}
