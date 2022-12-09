package com.nawilny.aoc2022.day09

import com.nawilny.aoc2022.common.Direction
import com.nawilny.aoc2022.common.Direction.*
import com.nawilny.aoc2022.common.Input
import com.nawilny.aoc2022.common.Point

fun main() {
    val lines = Input.readFileLinesNormalized("day09", "input.txt")
    val commands = lines.map { it.split(" ") }.map { Command(parseDirection(it[0]), it[1].toInt()) }

    println(calculateTailPath(commands, 1).size)
    println(calculateTailPath(commands, 9).size)
}

private fun calculateTailPath(commands: List<Command>, ropeLength: Int): Set<Point> {
    var nodes = List(ropeLength + 1) { Point(0, 0) }
    return commands.flatMap { command ->
        (0 until command.distance).map { _ ->
            var prev: Point? = null
            nodes = nodes.map {
                val next = if (prev == null) {
                    command.direction.next(it)
                } else {
                    nextTail(prev!!, it)
                }
                prev = next
                next
            }
            prev!!
        }
    }.toSet()
}

private fun nextTail(head: Point, tail: Point): Point {
    return when {
        head.y < tail.y - 1 -> {
            when {
                head.x < tail.x -> Point(tail.x - 1, tail.y - 1)
                head.x > tail.x -> Point(tail.x + 1, tail.y - 1)
                else -> Point(tail.x, tail.y - 1)
            }
        }
        head.y == tail.y - 1 -> {
            when {
                head.x < tail.x - 1 -> Point(tail.x - 1, tail.y - 1)
                head.x > tail.x + 1 -> Point(tail.x + 1, tail.y - 1)
                else -> tail
            }
        }
        head.y == tail.y -> {
            when {
                head.x < tail.x - 1 -> Point(tail.x - 1, tail.y)
                head.x > tail.x + 1 -> Point(tail.x + 1, tail.y)
                else -> tail
            }
        }
        head.y == tail.y + 1 -> {
            when {
                head.x < tail.x - 1 -> Point(tail.x - 1, tail.y + 1)
                head.x > tail.x + 1 -> Point(tail.x + 1, tail.y + 1)
                else -> tail
            }
        }
        head.y > tail.y + 1 -> {
            when {
                head.x < tail.x -> Point(tail.x - 1, tail.y + 1)
                head.x > tail.x -> Point(tail.x + 1, tail.y + 1)
                else -> Point(tail.x, tail.y + 1)
            }
        }
        else -> error("Unsupported case")
    }
}

private fun parseDirection(s: String): Direction {
    return when (s) {
        "U" -> UP
        "D" -> DOWN
        "L" -> LEFT
        "R" -> RIGHT
        else -> error("Unknown direction '$s'")
    }
}

private data class Command(val direction: Direction, val distance: Int)
