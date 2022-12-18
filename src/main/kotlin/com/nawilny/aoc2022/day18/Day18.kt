package com.nawilny.aoc2022.day18

import com.nawilny.aoc2022.common.Input

fun main() {
    val lavaPoints = Input.readFileLinesNormalized("day18", "input.txt").map { parseLine(it) }.toSet()

    println(lavaPoints.sumOf { p -> generateNeighbours(p).filter { !lavaPoints.contains(it) }.size })

    val minPoint = Point3D(lavaPoints.minOf { it.x } - 1, lavaPoints.minOf { it.y } - 1, lavaPoints.minOf { it.z } - 1)
    val maxPoint = Point3D(lavaPoints.maxOf { it.x } + 1, lavaPoints.maxOf { it.y } + 1, lavaPoints.maxOf { it.z } + 1)

    var area = 0
    val pointsToAnalyse = mutableSetOf(minPoint)
    val analysedPoints = mutableSetOf<Point3D>()
    while (pointsToAnalyse.isNotEmpty()) {
        val point = pointsToAnalyse.first()
        pointsToAnalyse.remove(point)
        generateNeighbours(point)
            .filter { it.isBetween(minPoint, maxPoint) }
            .filter { !analysedPoints.contains(it) }
            .forEach {
                if (lavaPoints.contains(it)) {
                    area++
                } else {
                    pointsToAnalyse.add(it)
                }
            }
        analysedPoints.add(point)
    }
    println(area)
}

private data class Point3D(val x: Int, val y: Int, val z: Int) {
    fun isBetween(p1: Point3D, p2: Point3D) = x >= p1.x && y >= p1.y && z >= p1.z && x <= p2.x && y <= p2.y && z <= p2.z
}

private fun generateNeighbours(p: Point3D): List<Point3D> {
    return listOf(
        Point3D(p.x - 1, p.y, p.z),
        Point3D(p.x + 1, p.y, p.z),
        Point3D(p.x, p.y - 1, p.z),
        Point3D(p.x, p.y + 1, p.z),
        Point3D(p.x, p.y, p.z - 1),
        Point3D(p.x, p.y, p.z + 1)
    )
}

private fun parseLine(line: String): Point3D {
    val coordinates = line.split(",").map { it.toInt() }
    return Point3D(coordinates[0], coordinates[1], coordinates[2])
}
