package com.nawilny.aoc2022.day24

import com.nawilny.aoc2022.common.Direction
import com.nawilny.aoc2022.common.Direction.*
import com.nawilny.aoc2022.common.Input
import com.nawilny.aoc2022.common.Point

fun main() {
    val input = Input.readFileLinesNormalized("day24", "input.txt")
    val valley = parse(input)

    val v1 = findPathLength(valley)
    val v2 = findPathLength(v1.second.reverseStartAndEnd())
    val v3 = findPathLength(v2.second.reverseStartAndEnd())

    println(v1.first)
    println(v1.first + v2.first + v3.first)
}

private fun findPathLength(valley: Valley): Pair<Int, Valley> {
    var currentCalley = valley
    var points = setOf(valley.start)
    var round = 0
    while (true) {
        round++
//        println(round)
        currentCalley = currentCalley.nextState()
        points = points.flatMap { currentCalley.movesFrom(it) }.toSet()
        if (points.isEmpty()) {
            error("solution not found")
        }
        if (points.contains(valley.end)) {
            return round to currentCalley
        }
    }
}

private data class Valley(
    val width: Int,
    val height: Int,
    val start: Point,
    val end: Point,
    val blizzards: List<Blizzard>
) {

    private val occupiedFields = blizzards.map { it.position }.toSet()
        .plus((0 until width).map { Point(it, 0) })
        .plus((0 until width).map { Point(it, height - 1) })
        .plus((1 until height).map { Point(0, it) })
        .plus((1 until height).map { Point(width - 1, it) })
        .minus(start).minus(end)

    fun nextState(): Valley {
        return Valley(width, height, start, end, blizzards.map { it.next(width, height) })
    }

    fun movesFrom(p: Point): List<Point> {
        return listOf(p, LEFT.next(p), RIGHT.next(p), UP.next(p), DOWN.next(p))
            .filter { !occupiedFields.contains(it) }
            .filter { it.x >= 0 && it.y >= 0 }
    }

    fun reverseStartAndEnd() = Valley(width, height, end, start, blizzards)

    fun print() {
        println((0 until width).map { if (Point(it, 0) == start) '.' else '#' }.joinToString(""))
        (1..height - 2).forEach { y ->
            print('#')
            (1..width - 2).map { Point(it, y) }.map { getChar(it) }.forEach { print(it) }
            print('#')
            println()
        }
        println((0 until width).map { if (Point(it, height - 1) == end) '.' else '#' }.joinToString(""))
    }

    private fun getChar(p: Point): Char {
        val c = blizzards.filter { it.position == p }
        return when {
            c.isEmpty() -> '.'
            c.size > 1 -> c.size.digitToChar()
            else -> when (c[0].direction) {
                UP -> '^'
                DOWN -> 'v'
                LEFT -> '<'
                RIGHT -> '>'
            }
        }
    }

}

private data class Blizzard(val position: Point, val direction: Direction) {
    fun next(width: Int, height: Int): Blizzard {
        val nextPosition = direction.next(position)
        return when {
            nextPosition.x == 0 -> Blizzard(Point(width - 2, nextPosition.y), direction)
            nextPosition.x == width - 1 -> Blizzard(Point(1, nextPosition.y), direction)
            nextPosition.y == 0 -> Blizzard(Point(nextPosition.x, height - 2), direction)
            nextPosition.y == height - 1 -> Blizzard(Point(nextPosition.x, 1), direction)
            else -> Blizzard(nextPosition, direction)
        }
    }
}

private fun parse(input: List<String>): Valley {
    val height = input.size
    val width = input.first().length
    val start = Point(input.first().withIndex().first { it.value == '.' }.index, 0)
    val end = Point(input.last().withIndex().first { it.value == '.' }.index, height - 1)
    val blizzardChars = mapOf(
        '<' to LEFT,
        '>' to RIGHT,
        '^' to UP,
        'v' to DOWN
    )
    val blizzards = input.withIndex().flatMap { line ->
        line.value.withIndex()
            .filter { blizzardChars.containsKey(it.value) }
            .map { Blizzard(Point(it.index, line.index), blizzardChars[it.value]!!) }
    }
    return Valley(width, height, start, end, blizzards)
}
