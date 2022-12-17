package com.nawilny.aoc2022.day17

import com.nawilny.aoc2022.common.Direction
import com.nawilny.aoc2022.common.Input
import com.nawilny.aoc2022.common.Point
import kotlin.math.max

const val caveLeft = 0
const val caveRight = 6

fun main() {
    val jetSequence = parseJetStreams(Input.readFileLinesNormalized("day17", "input.txt"))

//    val rocksNumber = 2022
    val rocksNumber = 1000000000000


    val cycle = findCycle(jetSequence)
    val totalCyclesCount = (rocksNumber - cycle.firstData.rocksCounter) / cycle.cycleData.rocksCounter
    val cyclesRocks = cycle.firstData.rocksCounter + (cycle.cycleData.rocksCounter * totalCyclesCount)
    val cyclesHeight = cycle.firstData.totalHeight + (cycle.cycleData.totalHeight * totalCyclesCount)
    val remainingRocks = (rocksNumber - cyclesRocks).toInt()
    val addedHeight = simulate(cycle.state, remainingRocks, jetSequence)
    println(cyclesHeight + addedHeight)
}

private fun simulate(state: State, rocksCount: Int, jetStreamsSequence: List<Direction>): Int {
    val rockGenerator = RockGenerator(state.rockSequence)
    val jetStreams = JetStreams(jetStreamsSequence, state.jetSequence)
    val fallenRocksMap = mutableMapOf<Int, MutableSet<Int>>()
    fallenRocksMap.addPoints(state.fallenRocks)
    var height = fallenRocksMap.keys.maxOrNull()!!
    var addedHeight = 0
    (1..rocksCount).forEach { _ ->
        val rock = rockGenerator.next(height)
        val fallenRock = simulateRockFalling(rock, fallenRocksMap, jetStreams)
        fallenRocksMap.addPoints(fallenRock)
        val newHeight = max(height, fallenRock.maxOf { it.y })
        addedHeight += newHeight - height
        height = newHeight
    }
    return addedHeight
}

private fun findCycle(jetStreamsSequence: List<Direction>): CycleData {
    var fallenRocksMap = mutableMapOf(0 to (caveLeft..caveRight).map { it }.toMutableSet())
    val jetStreams = JetStreams(jetStreamsSequence, 0)
    val rockGenerator = RockGenerator(0)
    var height = 0
    var totalHeight = 0
    val states = mutableMapOf(
        State(rockGenerator.seqNo, jetStreams.seqNo, fallenRocksMap.toPoints()) to StateMetadata(0, 0)
    )
    var rockCounter = 0

    while (true) {
        rockCounter++
        val rock = rockGenerator.next(height)
        val fallenRock = simulateRockFalling(rock, fallenRocksMap, jetStreams)
        fallenRocksMap.addPoints(fallenRock)
        val newHeight = max(height, fallenRock.maxOf { it.y })
        totalHeight += newHeight - height
        height = newHeight
        val maxReachableLevel = calculateMaxReachableLevel(fallenRocksMap, height)
        if (maxReachableLevel > 0) {
            height -= maxReachableLevel
            fallenRocksMap = fallenRocksMap.filter { it.key >= maxReachableLevel }
                .mapKeys { it.key - maxReachableLevel }
                .toMutableMap()
            val state = State(rockGenerator.seqNo, jetStreams.seqNo, fallenRocksMap.toPoints())
            if (states.contains(state)) {
                val previous = states[state]!!
                return CycleData(
                    previous,
                    StateMetadata(totalHeight - previous.totalHeight, rockCounter - previous.rocksCounter),
                    state
                )
            }
            states[state] = StateMetadata(totalHeight, rockCounter)
        }
    }
}

private data class State(val rockSequence: Int, val jetSequence: Int, val fallenRocks: Set<Point>)
private data class StateMetadata(val totalHeight: Int, val rocksCounter: Int)
private data class CycleData(val firstData: StateMetadata, val cycleData: StateMetadata, val state: State)

fun MutableMap<Int, MutableSet<Int>>.addPoints(points: Set<Point>) {
    points.forEach { p -> this.getOrPut(p.y) { mutableSetOf() }.add(p.x) }
}

fun MutableMap<Int, MutableSet<Int>>.toPoints() = this.flatMap { e -> e.value.map { Point(it, e.key) } }.toSet()

private fun calculateMaxReachableLevel(rocks: Map<Int, Set<Int>>, height: Int): Int {
    var maxReachableLevel = height + 1
    var row = (caveLeft..caveRight).toSet()
    while (true) {
        maxReachableLevel--
        val rocksInRow = rocks[maxReachableLevel] ?: emptySet()
        row = row.flatMap { x ->
            val s = mutableSetOf<Int>()
            if (!rocksInRow.contains(x)) s.add(x)
            if (!rocksInRow.contains(x - 1) && x - 1 >= caveLeft) s.add(x - 1)
            if (!rocksInRow.contains(x + 1) && x + 1 <= caveRight) s.add(x + 1)
            s
        }.toSet()
        if (row.isEmpty()) {
            return maxReachableLevel
        }
    }
}

private fun simulateRockFalling(rock: Set<Point>, fallenRocks: Map<Int, Set<Int>>, jetStreams: JetStreams): Set<Point> {
    var fallingRock = rock
    var nextPosition: Set<Point>?
    while (true) {
        val jetDirection = jetStreams.next()
        nextPosition = move(fallingRock, jetDirection, fallenRocks)
        if (nextPosition != null) {
            fallingRock = nextPosition
        }
        nextPosition = move(fallingRock, Direction.UP, fallenRocks) // Y axes is inverted
        if (nextPosition != null) {
            fallingRock = nextPosition
        } else {
            return fallingRock
        }
    }
}

private fun move(rock: Set<Point>, direction: Direction, fallenRocks: Map<Int, Set<Int>>): Set<Point>? {
    val nextPosition = rock.map { direction.next(it) }.toSet()
    return if (nextPosition.any { fallenRocks[it.y]?.contains(it.x) == true }
        || nextPosition.any { it.x > caveRight }
        || nextPosition.any { it.x < caveLeft }) {
        null
    } else {
        nextPosition
    }
}

private data class JetStreams(val streamsSequence: List<Direction>, var seqNo: Int) {
    fun next(): Direction {
        val direction = streamsSequence[seqNo]
        seqNo++
        if (seqNo >= streamsSequence.size) {
            seqNo = 0
        }
        return direction
    }
}

private data class RockGenerator(var seqNo: Int) {
    val d = 4
    fun next(h: Int): Set<Point> {
        val nextRock = when (seqNo) {
            0 -> setOf(Point(2, h + d), Point(3, h + d), Point(4, h + d), Point(5, h + d))
            1 -> setOf(
                Point(3, h + d), Point(2, h + d + 1), Point(3, h + d + 1), Point(4, h + d + 1), Point(3, h + d + 2)
            )
            2 -> setOf(Point(2, h + d), Point(3, h + d), Point(4, h + d), Point(4, h + d + 1), Point(4, h + d + 2))
            3 -> setOf(Point(2, h + d), Point(2, h + d + 1), Point(2, h + d + 2), Point(2, h + d + 3))
            4 -> setOf(Point(2, h + d), Point(3, h + d), Point(2, h + d + 1), Point(3, h + d + 1))
            else -> error("This is impossible")
        }
        seqNo++
        if (seqNo > 4) {
            seqNo = 0
        }
        return nextRock
    }
}

private fun parseJetStreams(input: List<String>): List<Direction> {
    return input.first().map {
        when (it) {
            '<' -> Direction.LEFT
            '>' -> Direction.RIGHT
            else -> error("Unknown direction '$it'")
        }
    }
}

private fun printCave(fallenRock: Map<Int, Set<Int>>) {
    val maxY = fallenRock.maxOf { it.key }
    ((maxY + 2) downTo 0).forEach { y ->
        print("|")
        val fallenRocksRow = fallenRock[y] ?: setOf()
        (caveLeft..caveRight).forEach { x ->
            print(if (fallenRocksRow.contains(x)) '#' else '.')
        }
        print("|")
        println()
    }
}
