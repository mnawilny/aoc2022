package com.nawilny.aoc2022.day15

import com.nawilny.aoc2022.common.Input
import com.nawilny.aoc2022.common.Point
import kotlin.math.max
import kotlin.math.min

fun main() {
    val sensors = Input.readFileLinesNormalized("day15", "input.txt").map { parse(it) }
    val beacons = sensors.map { it.closestBeacon }.toSet()

    val yValue = 2000000
    val minValue = 0
    val maxValue = 4000000
//    val maxValue = 20
//    val y = 10

    val positions = sensors.mapNotNull { it.getRange(yValue) }.flatMap { it.toSet() }.toSet()
    val positionsWithoutBeacons = positions.minus(beacons.filter { it.y == yValue }.map { it.x })
    println(positionsWithoutBeacons.size)

    val start = System.currentTimeMillis()
    (0..maxValue).forEach { y ->
        val ranges = combine(sensors.mapNotNull { it.getRangeWithin(y, minValue, maxValue) })
        if (ranges.size > 1 || !ranges.first().contains(minValue) || !ranges.first().contains(maxValue)) {
            val x = when {
                ranges.size > 1 -> {
                    val low = ranges.map { it.last }.first { it < maxValue }
                    low + 1
                }
                !ranges.first().contains(minValue) -> minValue
                !ranges.first().contains(maxValue) -> maxValue
                else -> error("Shouldn't happen")
            }
            println((4000000L * x) + y)
        }
    }
    val end = System.currentTimeMillis()
    println("time: " + (end - start) / 1000.0)
}

private fun combine(ranges: List<IntRange>): List<IntRange> {
    return ranges.fold(listOf()) { result, range ->
        var combined = range
        val other = mutableListOf<IntRange>()
        result.forEach { r ->
            if (
                r.contains(combined.first - 1) || r.contains(combined.last + 1)
                || combined.contains(r.first - 1) || combined.contains(r.last + 1)
            ) {
                combined = min(combined.first, r.first)..max(combined.last, r.last)
            } else {
                other.add(r)
            }
        }
        other.add(combined)
        other
    }
}

private data class Sensor(val position: Point, val closestBeacon: Point) {

    private val radius = position.manhattanDistanceTo(closestBeacon)

    fun getRange(y: Int): IntRange? {
        val center = position.x
        val centerDistance = max(y, position.y) - min(y, position.y)
        val remainingDistance = radius - centerDistance
        return if (remainingDistance >= 0) {
            (center - remainingDistance)..(center + remainingDistance)
        } else {
            null
        }
    }

    fun getRangeWithin(y: Int, minValue: Int, maxValue: Int): IntRange? {
        val center = position.x
        val centerDistance = max(y, position.y) - min(y, position.y)
        val remainingDistance = radius - centerDistance
        return if (remainingDistance >= 0) {
            max(minValue, (center - remainingDistance))..min(maxValue, (center + remainingDistance))
        } else {
            null
        }
    }
}

private fun parse(line: String): Sensor {
    val parts = line.split(":")
    val sensorParts = parts[0].split(",")
    val beaconParts = parts[1].split(",")
    val position = Point(sensorParts[0].substring(12).toInt(), sensorParts[1].substring(3).toInt())
    val closestBeacon = Point(beaconParts[0].substring(24).toInt(), beaconParts[1].substring(3).toInt())
    return Sensor(position, closestBeacon)
}
