package com.nawilny.aoc2022.day12

import com.nawilny.aoc2022.common.Direction
import com.nawilny.aoc2022.common.Input
import com.nawilny.aoc2022.common.Point

fun main() {
    val input = Input.readFileLinesNormalized("day12", "input.txt")
        .withIndex().flatMap { line ->
            line.value.withIndex().map { Point(it.index, line.index) to it.value }
        }.toMap()
    val start = input.filter { it.value == 'S' }.keys.first()
    val end = input.filter { it.value == 'E' }.keys.first()

    val grid = input
        .mapValues { if (it.value == 'S') 'a' else if (it.value == 'E') 'z' else it.value }
        .mapValues { it.value - 'a' }

    val path = findShortestPath(start, end, grid)
    println(path!!.size) // 391

    val starts = grid.filter { it.value == 0 }.keys
    println(starts.map { findShortestPath(it, end, grid) }.map { it?.size }.minByOrNull { it ?: Int.MAX_VALUE }) // 386
}

fun findShortestPath(start: Point, end: Point, grid: Map<Point, Int>): List<Point>? {
    val unvisitedSet = grid.keys.toMutableSet()
    val paths = mutableMapOf(start to emptyList<Point>())

    while (unvisitedSet.isNotEmpty()) {
        val currentNode = unvisitedSet.map { it to (paths[it]?.size ?: Int.MAX_VALUE) }
            .minByOrNull { it.second }!!.first
        if (!paths.contains(currentNode)) {
            return null
        }
        val currentPath = paths[currentNode]!!
        Direction.values().map { it.next(currentNode) }
            .filter { grid.contains(it) && grid[it]!! <= grid[currentNode]!! + 1 }
            .filter { paths[it] == null || paths[it]!!.size > currentPath.size }
            .forEach { paths[it] = currentPath.plus(it) }
        unvisitedSet.remove(currentNode)
        if (currentNode == end) {
            return currentPath
        }
    }
    return null
}

