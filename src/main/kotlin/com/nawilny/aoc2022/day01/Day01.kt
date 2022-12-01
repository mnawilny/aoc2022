package com.nawilny.aoc2022.day01

import com.nawilny.aoc2022.common.Input

fun main() {
    val elves = parse(Input.readFileLines("day01", "input1.txt"))
    println(elves)
    println(elves.map { it.calories() }.maxOrNull())
    println(elves.map { it.calories() }.sortedDescending().take(3).sum())
}

data class Elf(val bag: List<Int>) {
    fun calories() = bag.sum()
}

private fun parse(lines: List<String>): List<Elf> {
    val elves = mutableListOf<Elf>()
    var currentBag = mutableListOf<Int>()
    lines.map { it.trim() }.forEach { line ->
        if (line.isEmpty()) {
            if (currentBag.isNotEmpty()) {
                elves.add(Elf(currentBag))
                currentBag = mutableListOf()
            }
        } else {
            currentBag.add(line.toInt())
        }
    }
    if (currentBag.isNotEmpty()) {
        elves.add(Elf(currentBag))
    }
    return elves
}
