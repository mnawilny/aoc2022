package com.nawilny.aoc2022.day02

import com.nawilny.aoc2022.common.Input
import com.nawilny.aoc2022.day02.Move.*
import com.nawilny.aoc2022.day02.Outcome.*

fun main() {
    val moves = Input.readFileLines("day02", "input.txt")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { it.split(" ") }
        .map { it[0] to it[1] }

    println(moves)
    println(moves
        .map { inputMap1[it.first]!! to inputMap2[it.second]!! }
        .sumOf { score(it) }
    )
    println(moves
        .map {
            val m1 = inputMap1[it.first]!!
            val outcome = outcomeMap[it.second]!!
            val m2 = selectMove(m1, outcome)
            m1 to m2
        }
        .sumOf { score(it) }
    )
}

private enum class Move(val score: Int) {
    ROCK(1), PAPER(2), SCISSORS(3)
}

private enum class Outcome(val score: Int) {
    WIN(6), LOOSE(0), DRAW(3)
}

private fun selectMove(move: Move, outcome: Outcome): Move {
    return when (move) {
        ROCK -> when (outcome) {
            WIN -> PAPER
            LOOSE -> SCISSORS
            DRAW -> ROCK
        }
        PAPER -> when (outcome) {
            WIN -> SCISSORS
            LOOSE -> ROCK
            DRAW -> PAPER
        }
        SCISSORS -> when (outcome) {
            WIN -> ROCK
            LOOSE -> PAPER
            DRAW -> SCISSORS
        }
    }
}

private fun score(moves: Pair<Move, Move>) = outcome(moves.first, moves.second).score + moves.second.score

private fun outcome(m1: Move, m2: Move): Outcome {
    return when (m1) {
        ROCK -> when (m2) {
            ROCK -> DRAW
            PAPER -> WIN
            SCISSORS -> LOOSE
        }
        PAPER -> when (m2) {
            ROCK -> LOOSE
            PAPER -> DRAW
            SCISSORS -> WIN
        }
        SCISSORS -> when (m2) {
            ROCK -> WIN
            PAPER -> LOOSE
            SCISSORS -> DRAW
        }
    }
}

private val inputMap1 = listOf("A" to ROCK, "B" to PAPER, "C" to SCISSORS).toMap()
private val inputMap2 = listOf("X" to ROCK, "Y" to PAPER, "Z" to SCISSORS).toMap()
private val outcomeMap = listOf("X" to LOOSE, "Y" to DRAW, "Z" to WIN).toMap()
