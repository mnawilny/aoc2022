package com.nawilny.aoc2022.day14

import com.nawilny.aoc2022.common.Input
import com.nawilny.aoc2022.common.Point
import kotlin.math.max
import kotlin.math.min

fun main() {
    val input = Input.readFileLinesNormalized("day14", "input.txt")
    val paths = input.map { it.split(" -> ") }.map { line ->
        line.map { it.split(",") }.map { Point(it[0].toInt(), it[1].toInt()) }
    }
    val rocks = getAllRocks(paths)

    // part1
//    val grid = rocks.associateWith { '#' }.toMutableMap()
    // part1
    val grid = rocks.plus(generateFloor(rocks)).associateWith { '#' }.toMutableMap()

    var fallenSand = simulateSandFalling(grid)
    while (fallenSand != null) {
        grid[fallenSand] = 'o'
        fallenSand = simulateSandFalling(grid)
    }

    printGrid(grid)
    println(grid.filter { it.value == 'o' }.count())
}

private fun simulateSandFalling(grid: Map<Point, Char>): Point? {
    var sandPoint = Point(500, 0)
    if (grid.containsKey(sandPoint)) {
        return null
    }
    val maxy = grid.keys.maxOf { it.y }
    while (sandPoint.y <= maxy) {
        val down = Point(sandPoint.x, sandPoint.y + 1)
        val downLeft = Point(sandPoint.x - 1, sandPoint.y + 1)
        val downRight = Point(sandPoint.x + 1, sandPoint.y + 1)
        sandPoint = when {
            !grid.containsKey(down) -> down
            !grid.containsKey(downLeft) -> downLeft
            !grid.containsKey(downRight) -> downRight
            else -> return sandPoint
        }
    }
    return null
}

private fun getAllRocks(paths: List<List<Point>>): Set<Point> {
    return paths.flatMap { lines ->
        var previous = lines.first()
        val path = mutableSetOf<Point>()
        lines.drop(1).forEach { point ->
            val linePoints = if (previous.x == point.x) {
                (min(point.y, previous.y)..max(point.y, previous.y)).map { Point(point.x, it) }
            } else {
                (min(point.x, previous.x)..max(point.x, previous.x)).map { Point(it, point.y) }
            }
            path.addAll(linePoints)
            previous = point
        }
        path
    }.toSet()
}

private fun printGrid(grid: Map<Point, Char>) {
    val minx = grid.keys.minOf { it.x }
    val miny = grid.keys.minOf { it.y }
    val maxx = grid.keys.maxOf { it.x }
    val maxy = grid.keys.maxOf { it.y }
    (miny..maxy).forEach { y ->
        (minx..maxx).forEach { x ->
            print(grid[Point(x, y)] ?: '.')
        }
        println()
    }
}

private fun generateFloor(rocks: Set<Point>): List<Point> {
    val y = rocks.maxOf { it.y } + 2
    val minx = rocks.minOf { it.x } - 200
    val maxx = rocks.maxOf { it.x } + 200
    return (minx..maxx).map { Point(it, y) }
}
