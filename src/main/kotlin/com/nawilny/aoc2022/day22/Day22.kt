package com.nawilny.aoc2022.day22

import com.nawilny.aoc2022.common.Direction
import com.nawilny.aoc2022.common.Direction.*
import com.nawilny.aoc2022.common.Input
import com.nawilny.aoc2022.common.Point

fun main() {
//    val lines = Input.readFileLines("day22", "example_w_metadata.txt").filter { it.isNotEmpty() }
    val lines = Input.readFileLines("day22", "input_w_metadata.txt").filter { it.isNotEmpty() }

    val cubeSide = lines.first().toInt()
    val sidesInfo = lines.drop(1).take(6).map { parseSideInfo(it) }.associateBy { it.name }
    val moves = parseMoves(lines.last())

    val boardLines = normalize(lines.drop(7).dropLast(1))

    val board1 = FlatBoardMap(boardLines)
    val endPosition1 = executeMoves(board1, moves)
    println(getPassword(endPosition1))

    val board2 = CubeBoardMap(cubeSide, sidesInfo, boardLines)
    val endPosition2 = executeMoves(board2, moves)
    println(getPassword(endPosition2))
}

private class CubeBoardMap(
    val cubeSide: Int,
    val sidesInfo: Map<Side, CubeSideInfo>,
    lines: List<String>
) : BoardMap(lines) {

    override fun jumpVoid(p: Position): Position {
        val sidePosition = getSide(p.point)
        val currentSide = sidePosition.first
        val currentSidePosition = sidePosition.second
        val rotatedPosition = rotate(Position(currentSidePosition, p.rotation), currentSide.relativePosition.rotation)
        val nextSidePosition = currentSide.name.nextSide(rotatedPosition.rotation)
        val nextSide = sidesInfo[nextSidePosition.first]!!
        val nextPosition = rotate(rotatedPosition, nextSidePosition.second)
        val oppositeSidePosition = rollToOppositeSideSide(nextPosition)
        val rotatedOppositeSidePosition = reversedRotation(oppositeSidePosition, nextSide.relativePosition.rotation)
        return translateToAbsoluteValues(rotatedOppositeSidePosition, nextSide)
    }

    fun getSide(p: Point): Pair<CubeSideInfo, Point> {
        val sidePoint = Point((p.x - 1) / cubeSide, (p.y - 1) / cubeSide)
        val side =
            sidesInfo.values.find { it.relativePosition.point == sidePoint } ?: error("Side for point $p not found")
        return side to Point((p.x - 1) % cubeSide, (p.y - 1) % cubeSide)
    }

    fun rotate(p: Position, sideRotation: Direction): Position {
        return when (sideRotation) {
            UP -> p
            RIGHT -> Position(Point(cubeSide - 1 - p.point.y, p.point.x), rotate(p.rotation, RIGHT))
            DOWN -> Position(Point(cubeSide - 1 - p.point.x, cubeSide - 1 - p.point.y), rotate(p.rotation, DOWN))
            LEFT -> Position(Point(p.point.y, cubeSide - 1 - p.point.x), rotate(p.rotation, LEFT))
        }
    }

    fun reversedRotation(p: Position, sideRotation: Direction): Position {
        return rotate(
            p, when (sideRotation) {
                LEFT -> RIGHT
                RIGHT -> LEFT
                else -> sideRotation
            }
        )
    }

    fun rollToOppositeSideSide(p: Position): Position {
        val point = when (p.rotation) {
            UP -> Point(p.point.x, p.point.y + (cubeSide - 1))
            RIGHT -> Point(p.point.x - (cubeSide - 1), p.point.y)
            DOWN -> Point(p.point.x, p.point.y - (cubeSide - 1))
            LEFT -> Point(p.point.x + (cubeSide - 1), p.point.y)
        }
        return Position(point, p.rotation)
    }

    fun translateToAbsoluteValues(p: Position, side: CubeSideInfo): Position {
        return Position(
            Point(
                (side.relativePosition.point.x * cubeSide) + p.point.x + 1,
                (side.relativePosition.point.y * cubeSide) + p.point.y + 1
            ), p.rotation
        )
    }

}

private fun executeMoves(board: BoardMap, moves: List<Move>): Position {
    var position = Position(board.getStart(), RIGHT)
    moves.forEach { move ->
        position = when (move) {
            is Rotate -> Position(position.point, rotate(position.rotation, move.direction))
            is Forward -> board.moveForward(position, move.distance)
        }
        println("------Moved to $position")
    }
    return position
}

private fun rotate(p: Direction, rotation: Direction): Direction {
    return when (rotation) {
        RIGHT -> when (p) {
            UP -> RIGHT
            RIGHT -> DOWN
            DOWN -> LEFT
            LEFT -> UP
        }
        LEFT -> when (p) {
            UP -> LEFT
            LEFT -> DOWN
            DOWN -> RIGHT
            RIGHT -> UP
        }
        DOWN -> when (p) {
            UP -> DOWN
            LEFT -> RIGHT
            DOWN -> UP
            RIGHT -> LEFT
        }
        else -> error("Unsupported rotation direction '$rotation'")
    }
}

abstract class BoardMap(val lines: List<String>) {

    fun getStart(): Point {
        val x = lines[1].withIndex().find { it.value == '.' }!!.index
        return Point(x, 1)
    }

    fun moveForward(p: Position, distance: Int): Position {
        var newPoint = p
        (1..distance).forEach { i ->
            var nextCandidate = Position(newPoint.rotation.next(newPoint.point), newPoint.rotation)
            if (lines[nextCandidate.point.y][nextCandidate.point.x] == ' ') {
                nextCandidate = jumpVoid(newPoint)
            }
            if (lines[nextCandidate.point.y][nextCandidate.point.x] == '#') {
                return newPoint
            }
            newPoint = nextCandidate
        }
        return newPoint
    }

    abstract fun jumpVoid(p: Position): Position

}

private class FlatBoardMap(lines: List<String>) : BoardMap(lines) {

    override fun jumpVoid(p: Position): Position {
        val next = when (p.rotation) {
            UP -> Point(
                p.point.x,
                lines.withIndex().findLast { it.value[p.point.x] != ' ' }!!.index,
            )
            DOWN -> Point(
                p.point.x,
                lines.withIndex().find { it.value[p.point.x] != ' ' }!!.index,
            )
            LEFT -> Point(
                lines[p.point.y].withIndex().findLast { it.value != ' ' }!!.index,
                p.point.y
            )
            RIGHT -> Point(
                lines[p.point.y].withIndex().find { it.value != ' ' }!!.index,
                p.point.y
            )
        }
        return Position(next, p.rotation)
    }
}

data class Position(val point: Point, val rotation: Direction)

sealed class Move
data class Rotate(val direction: Direction) : Move()
data class Forward(val distance: Int) : Move()

fun parseMoves(s: String): List<Move> {
    val moves = mutableListOf<Move>()
    var n = ""
    s.forEach {
        when (it) {
            'R' -> {
                if (n.isNotEmpty()) {
                    moves.add(Forward(n.toInt()))
                    n = ""
                }
                moves.add(Rotate(RIGHT))
            }
            'L' -> {
                if (n.isNotEmpty()) {
                    moves.add(Forward(n.toInt()))
                    n = ""
                }
                moves.add(Rotate(LEFT))
            }
            else -> {
                n = n.plus(it)
            }
        }
    }
    if (n.isNotEmpty()) {
        moves.add(Forward(n.toInt()))
    }
    return moves
}

fun normalize(lines: List<String>): List<String> {
    val maxLength = lines.maxOf { it.length } + 2
    val emptyLine = (1..maxLength).joinToString("") { " " }
    val result = mutableListOf(emptyLine)
    lines.forEach { line ->
        var l = " $line "
        if (l.length < maxLength) {
            (1..(maxLength - l.length)).forEach { _ -> l = l.plus(" ") }
        }
        result.add(l)
    }
    result.add(emptyLine)
    return result
}

fun getPassword(position: Position): Int {
    return (1000 * position.point.y) + (4 * position.point.x) + when (position.rotation) {
        RIGHT -> 0
        DOWN -> 1
        LEFT -> 2
        UP -> 3
    }
}

// Metadata assumes a cube sides to be
// +----+
// | S5 |
// +----+----+----+----+
// | S1 | S2 | S3 | S4 |
// +----+----+----+----+
// | S6 |
// +----+

private enum class Side(val nextSide: (Direction) -> Pair<Side, Direction>) {
    S1({
        when (it) {
            UP -> S5 to UP
            RIGHT -> S2 to UP
            DOWN -> S6 to UP
            LEFT -> S4 to UP
        }
    }),
    S2({
        when (it) {
            UP -> S5 to LEFT
            RIGHT -> S3 to UP
            DOWN -> S6 to RIGHT
            LEFT -> S1 to UP
        }
    }),
    S3({
        when (it) {
            UP -> S5 to DOWN
            RIGHT -> S4 to UP
            DOWN -> S6 to DOWN
            LEFT -> S2 to UP
        }
    }),
    S4({
        when (it) {
            UP -> S5 to RIGHT
            RIGHT -> S1 to UP
            DOWN -> S6 to LEFT
            LEFT -> S3 to UP
        }
    }),
    S5({
        when (it) {
            UP -> S3 to DOWN
            RIGHT -> S2 to RIGHT
            DOWN -> S1 to UP
            LEFT -> S4 to LEFT
        }
    }),
    S6({
        when (it) {
            UP -> S1 to UP
            RIGHT -> S2 to LEFT
            DOWN -> S3 to DOWN
            LEFT -> S4 to RIGHT
        }
    }),
}

private data class CubeSideInfo(val name: Side, val relativePosition: Position)

private fun parseSideInfo(line: String): CubeSideInfo {
    val parts = line.split(" - ")
    val name = Side.valueOf(parts[0])
    val point = Point(parts[1].split(",")[0].toInt(), parts[1].split(",")[1].toInt())
    val rotation = when (parts[2]) {
        "0" -> UP
        "90" -> RIGHT
        "180" -> DOWN
        "270" -> LEFT
        else -> error("Unsupported cube side rotation '${parts[2]}'")
    }
    return CubeSideInfo(name, Position(point, rotation))
}
