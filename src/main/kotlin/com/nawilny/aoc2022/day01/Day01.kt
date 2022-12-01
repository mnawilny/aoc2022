package com.nawilny.aoc2022.day01

import com.nawilny.aoc2022.common.Input

fun main() {
    val elves = parse(Input.readFileLines("day01", "input1.txt"))
    println(elves)
    println(elves.map { it.sum() }.maxOrNull())
    println(elves.map { it.sum() }.sortedDescending().take(3).sum())
}

private fun parse(lines: List<String>): List<List<Int>> {
    val elves = mutableListOf<List<Int>>()
    var current = mutableListOf<Int>()
    lines.map { it.trim() }.forEach { line ->
        if (line.isEmpty()) {
            if (!current.isEmpty()) {
                elves.add(current)
                current = mutableListOf<Int>()
            }
        } else {
            current.add(line.toInt())
        }
    }
    if (!current.isEmpty()) {
        elves.add(current)
    }
    return elves
}
