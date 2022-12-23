package com.nawilny.aoc2022.day23

import com.nawilny.aoc2022.common.Input
import com.nawilny.aoc2022.common.Point
import com.nawilny.aoc2022.day23.Direction.*

fun main() {
    val elves = parseElves(Input.readFileLinesNormalized("day23", "input.txt"))

    var e1 = elves
    (0..9).forEach { round ->
        val moves = proposeMoves(round, e1)
        e1 = executeMoves(moves)
    }
    println(calculateEmptySpaces(e1))

    var e2previous = elves
    var e2next = executeMoves(proposeMoves(0, e2previous))
    var round = 1
    while (e2previous != e2next) {
        e2previous = e2next
        e2next = executeMoves(proposeMoves(round, e2next))
        round++
    }
    println(round)
}

private fun proposeMoves(round: Int, elves: Set<Point>): Map<Point, Point> {
    return elves.associateWith { proposeMove(round, it, elves) }
}

private fun proposeMove(round: Int, elf: Point, elves: Set<Point>): Point {
    if (Direction.values().all { !elves.contains(it.next(elf)) }) {
        return elf
    }
    moves.indices.forEach { i ->
        val move = moves[(round + i) % moves.size]
        if (move.first.all { !elves.contains(it.next(elf)) }) {
            return move.second.next(elf)
        }
    }
    return elf
}

private fun executeMoves(moves: Map<Point, Point>): Set<Point> {
    val duplicates = findDuplicates(moves.values)
    return moves.toList().map { if (duplicates.contains(it.second)) it.first else it.second }.toSet()
}

private fun findDuplicates(elements: Collection<Point>): Set<Point> {
    return elements.groupBy { it }.filter { it.value.size > 1 }.map { it.key }.toSet()
}

private val moves = listOf(
    listOf(N, NE, NW) to N,
    listOf(S, SE, SW) to S,
    listOf(W, NW, SW) to W,
    listOf(E, NE, SE) to E
)

private enum class Direction(val next: (Point) -> Point) {
    N({ Point(it.x, it.y - 1) }),
    S({ Point(it.x, it.y + 1) }),
    W({ Point(it.x - 1, it.y) }),
    E({ Point(it.x + 1, it.y) }),
    NW({ Point(it.x - 1, it.y - 1) }),
    NE({ Point(it.x + 1, it.y - 1) }),
    SW({ Point(it.x - 1, it.y + 1) }),
    SE({ Point(it.x + 1, it.y + 1) })
}

private fun calculateEmptySpaces(elves: Set<Point>): Int {
    val width = elves.maxOf { it.x } - elves.minOf { it.x } + 1
    val height = elves.maxOf { it.y } - elves.minOf { it.y } + 1
    return (width * height) - elves.size
}

private fun parseElves(lines: List<String>): Set<Point> {
    return lines.withIndex().flatMap { line ->
        line.value.withIndex().filter { it.value == '#' }.map { Point(it.index, line.index) }
    }.toSet()
}

private fun printElves(elves: Set<Point>) {
    for (y in elves.minOf { it.y }..elves.maxOf { it.y }) {
        for (x in elves.minOf { it.x }..elves.maxOf { it.x }) {
            print(if (elves.contains(Point(x, y))) '#' else '.')
        }
        println()
    }
}
