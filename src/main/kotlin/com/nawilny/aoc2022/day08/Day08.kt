package com.nawilny.aoc2022.day08

import com.nawilny.aoc2022.common.Input

fun main() {
    val lines = Input.readFileLinesNormalized("day08", "input.txt")
    val trees = lines.withIndex()
        .flatMap { line -> line.value.withIndex().map { Pair(Point(it.index, line.index), it.value.digitToInt()) } }
        .toMap()

    println(trees.keys.count { isVisible(it, trees) })

    println(trees.keys.map { calculateScenicScore(it, trees) }.maxOrNull())
}

private fun isVisible(p: Point, trees: Map<Point, Int>): Boolean {
    val height = trees[p]!!
    Direction.values().forEach { direction ->
        var itPoint = direction.next(p)
        while (trees.contains(itPoint) && trees[itPoint]!! < height) {
            itPoint = direction.next(itPoint)
        }
        if (!trees.contains(itPoint)) {
            return true
        }
    }
    return false
}

private fun calculateScenicScore(p: Point, trees: Map<Point, Int>): Int {
    return Direction.values()
        .map { calculateScenicScoreInDirection(p, it, trees) }
        .fold(1) { acc, i -> acc * i }
}

private fun calculateScenicScoreInDirection(p: Point, direction: Direction, trees: Map<Point, Int>): Int {
    var score = 0
    val height = trees[p]!!
    var itPoint = direction.next(p)
    while (trees.contains(itPoint) && trees[itPoint]!! < height) {
        itPoint = direction.next(itPoint)
        score++
    }
    if (trees.contains(itPoint)) {
        score++
    }
    return score
}

private data class Point(val x: Int, val y: Int)

private enum class Direction(val next: (Point) -> Point) {
    UP({ Point(it.x, it.y - 1) }),
    DOWN({ Point(it.x, it.y + 1) }),
    LEFT({ Point(it.x - 1, it.y) }),
    RIGHT({ Point(it.x + 1, it.y) })
}
