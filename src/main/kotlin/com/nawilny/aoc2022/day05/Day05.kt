package com.nawilny.aoc2022.day05

import com.nawilny.aoc2022.common.Input

fun main() {
    val input = Input.divideByNewLines(Input.readFileLines("day05", "input.txt"))

    val moves = parseMoves(input[1])

    val stacks = parseStacks(input[0])
    stacks.forEach { println(it.elements) }

    moves.forEach { move ->
        val fromStack = stacks[move.from - 1]
        val toStack = stacks[move.to - 1]
//        val elements = fromStack.take(move.quantity) // part 1
        val elements = fromStack.takeInOrder(move.quantity) // part 2
        println("------")
        println(move)
        toStack.put(elements)
        stacks.forEach { println(it.elements) }
    }
    println("------")
    println(stacks.map { it.elements.last() }.joinToString(""))
}

private class Stack {
    var elements = mutableListOf<Char>()

    fun put(el: Char) {
        elements.add(el)
    }

    fun put(el: List<Char>) {
        elements.addAll(el)
    }

    fun take(n: Int): List<Char> {
        val result = mutableListOf<Char>()
        for (i in 0 until n) {
            result.add(elements.last())
            elements.removeLast()
        }
        return result
    }

    fun takeInOrder(n: Int): List<Char> {
        return take(n).reversed()
    }
}

private fun parseStacks(stacksInput: List<String>): List<Stack> {
    val reversed = stacksInput.reversed()
    val indexes = reversed.first().withIndex()
        .filter { !it.value.isWhitespace() }
        .map { it.value.digitToInt() to it.index }
    val stacks = indexes.map { Stack() }

    reversed.drop(1).forEach { line ->
        indexes.map { it.first to line.getOrNull(it.second) }
            .filter { it.second != null && !it.second!!.isWhitespace() }
            .forEach { pair -> stacks[pair.first - 1].put(pair.second!!) }
    }

    return stacks
}

private data class Move(val quantity: Int, val from: Int, val to: Int)

private fun parseMoves(movesInput: List<String>): List<Move> {
    return movesInput
        .map { it.split(" ") }
        .map { Move(it[1].toInt(), it[3].toInt(), it[5].toInt()) }
}
