package codes.jakob.aoc.shared

/**
 * This class represents a direction on a 2D grid.
 */
enum class SimpleDirection(val expanded: ExpandedDirection) {
    NORTH(ExpandedDirection.NORTH),
    EAST(ExpandedDirection.EAST),
    SOUTH(ExpandedDirection.SOUTH),
    WEST(ExpandedDirection.WEST)
}

/**
 * This class represents a direction on a 2D grid, including the diagonals.
 */
enum class ExpandedDirection {
    NORTH,
    EAST,
    SOUTH,
    WEST,
    NORTH_EAST,
    NORTH_WEST,
    SOUTH_EAST,
    SOUTH_WEST
}
