package codes.jakob.aoc.shared

import codes.jakob.aoc.shared.ExpandedDirection.*


/**
 * A grid is a two-dimensional collection of cells.
 * Each cell has a value and a set of coordinates.
 * The coordinates are used to identify the cell in the grid.
 * The value is lazily evaluated.
 *
 * @param T The type of the value of each cell
 */
@Suppress("MemberVisibilityCanBePrivate")
class Grid<T>(input: List<List<(Cell<T>) -> T>>) {
    constructor(
        coordinateValues: Map<Coordinates, (Cell<T>) -> T>,
        defaultValueConstructor: (Cell<T>) -> T,
    ) : this(fromCoordinatesValues(coordinateValues, defaultValueConstructor))

    val matrix: Matrix<T> = generateMatrix(input)
    val cells: LinkedHashSet<Cell<T>> = LinkedHashSet(matrix.flatten())

    fun getAtCoordinates(coordinates: Coordinates): Cell<T>? = matrix.getOrNull(coordinates.y)?.getOrNull(coordinates.x)

    fun getAdjacent(coordinates: Coordinates, diagonally: Boolean = false): List<Cell<T>> {
        return listOfNotNull(
            getInDirection(coordinates, NORTH),
            if (diagonally) getInDirection(coordinates, NORTH_EAST) else null,
            getInDirection(coordinates, EAST),
            if (diagonally) getInDirection(coordinates, SOUTH_EAST) else null,
            getInDirection(coordinates, SOUTH),
            if (diagonally) getInDirection(coordinates, SOUTH_WEST) else null,
            getInDirection(coordinates, WEST),
            if (diagonally) getInDirection(coordinates, NORTH_WEST) else null,
        )
    }

    fun getInDirection(coordinates: Coordinates, direction: ExpandedDirection): Cell<T>? {
        return getAtCoordinates(coordinates.inDirection(direction))
    }

    fun <R> map(block: (Cell<T>) -> R): Grid<R> = fromMatrix(matrix.map { row -> row.map(block) })

    fun map(coordinates: Coordinates, block: (Cell<T>) -> T): Grid<T> {
        val desired: Cell<T> = getAtCoordinates(coordinates) ?: error("Coordinates do not exist in this grid")
        return fromMatrix(matrix.map { row ->
            row.map { cell -> if (desired == cell) block(cell) else cell.content.value }
        })
    }

    fun <R1, R2> reduce(
        direction: SimpleDirection,
        outer: (List<Cell<T>>) -> R1,
        inner: (List<R1>) -> R2,
    ): R2 {
        val matrixInDirection: Matrix<T> = when (direction) {
            SimpleDirection.NORTH -> matrix.transpose()
            SimpleDirection.EAST -> matrix.map { it.reversed() }
            SimpleDirection.SOUTH -> matrix.transpose().map { it.reversed() }
            SimpleDirection.WEST -> matrix
        }
        return inner(matrixInDirection.map(outer))
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

        val content: Lazy<T> = lazy { valueConstructor(this) }

        fun getAdjacent(diagonally: Boolean = false): List<Cell<T>> {
            return grid.getAdjacent(coordinates, diagonally)
        }

        fun distanceTo(other: Cell<T>, diagonally: Boolean = false): Int {
            require(other in grid.cells) { "Start and end point are not in the same grid" }
            return this.coordinates.distanceTo(other.coordinates, diagonally)
        }

        fun getInDirection(direction: ExpandedDirection): Cell<T>? {
            return grid.getInDirection(coordinates, direction)
        }

        fun <R> mapInDirection(direction: ExpandedDirection, block: (Cell<T>) -> R): List<R> {
            val mapped: MutableList<R> = mutableListOf()
            var currentCell: Cell<T>? = this
            while (currentCell != null) {
                currentCell = currentCell.getInDirection(direction)
                if (currentCell != null) mapped += block(currentCell)
            }
            return mapped
        }

        fun <R> foldInDirection(
            direction: ExpandedDirection,
            initial: R,
            accumulator: (R, Cell<T>) -> R,
        ): R {
            var reduced: R = initial
            var currentCell: Cell<T>? = this
            while (currentCell != null) {
                currentCell = currentCell.getInDirection(direction)
                if (currentCell != null) reduced = accumulator(reduced, currentCell)
            }
            return reduced
        }

        fun <R> foldInEveryDirection(
            initial: R,
            accumulator: (R, Cell<T>) -> R,
        ): Map<ExpandedDirection, R> {
            return entries.associateWith { this.foldInDirection(it, initial, accumulator) }
        }

        override fun toString(): String {
            return "Cell(value=$content, coordinates=$coordinates)"
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

    companion object {
        fun <T> fromMatrix(matrix: List<List<T>>): Grid<T> {
            return Grid(matrix.map { inner -> inner.map { value -> { value } } })
        }

        fun <T> fromCoordinatesValues(
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
    }
}

typealias Matrix<T> = List<List<Grid.Cell<T>>>
