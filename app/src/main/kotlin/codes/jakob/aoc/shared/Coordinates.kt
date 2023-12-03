package codes.jakob.aoc.shared

import codes.jakob.aoc.shared.ExpandedDirection.*
import kotlin.math.abs
import kotlin.math.max

/**
 * Represents a coordinate on a two-dimensional grid.
 */
data class Coordinates(
    val x: Int,
    val y: Int,
) {
    /**
     * Calculates the distance between two coordinates.
     */
    infix fun distanceTo(other: Coordinates): Int {
        return distanceTo(other, false)
    }

    /**
     * Calculates the distance between two coordinates diagonally.
     */
    infix fun distanceToDiagonally(other: Coordinates): Int {
        return distanceTo(other, true)
    }

    /**
     * Calculates the distance between two coordinates.
     *
     * @param other The other coordinate.
     * @param diagonally Whether to calculate the distance diagonally or not. Defaults to false.
     */
    fun distanceTo(other: Coordinates, diagonally: Boolean): Int {
        val xDistance: Int = abs(other.x - this.x)
        val yDistance: Int = abs(other.y - this.y)
        if (diagonally) return max(xDistance, yDistance)  // Chebyshev Distance
        return xDistance + yDistance  // Manhattan Distance
    }

    /**
     * Returns the coordinate in the given direction.
     */
    fun inDirection(direction: ExpandedDirection, distance: Int = 1): Coordinates {
        return when (direction) {
            NORTH -> Coordinates(x, y + distance)
            NORTH_EAST -> Coordinates(x + distance, y + distance)
            EAST -> Coordinates(x + distance, y)
            SOUTH_EAST -> Coordinates(x + distance, y - distance)
            SOUTH -> Coordinates(x, y - distance)
            SOUTH_WEST -> Coordinates(x - distance, y - distance)
            WEST -> Coordinates(x - distance, y)
            NORTH_WEST -> Coordinates(x - distance, y + distance)
        }
    }

    /**
     * Returns the direction in which the other coordinate is located.
     */
    fun inWhichDirection(other: Coordinates): ExpandedDirection? {
        if (other.x != x && other.y != y) {
            // Diagonal
            if (other.x - this.x > 0 && other.y - this.y > 0) {
                return NORTH_EAST
            } else if (other.x - this.x > 0 && other.y - this.y < 0) {
                return SOUTH_EAST
            } else if (other.x - this.x < 0 && other.y - this.y < 0) {
                return SOUTH_WEST
            } else if (other.x - this.x < 0 && other.y - this.y > 0) {
                return NORTH_WEST
            }
        } else {
            if (other.x != this.x) {
                if (other.x - this.x > 0) {
                    return EAST
                } else if (other.x - this.x < 0) {
                    return WEST
                }
            } else if (other.y != this.y) {
                if (other.y - this.y > 0) {
                    return NORTH
                } else if (other.y - this.y < 0) {
                    return SOUTH
                }
            }
        }
        return null
    }
}
