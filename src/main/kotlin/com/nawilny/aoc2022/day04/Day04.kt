package com.nawilny.aoc2022.day04

import com.nawilny.aoc2022.common.Input

fun main() {
    val pairs = parse(Input.readFileLinesNormalized("day04", "input.txt"))
    println(pairs.count { containsAll(it.first, it.second) || containsAll(it.second, it.first) })
    println(pairs.count { containsAny(it.first, it.second) || containsAny(it.second, it.first) })
}

private fun containsAll(r1: IntRange, r2: IntRange) = r1.contains(r2.first) && r1.contains(r2.last)
private fun containsAny(r1: IntRange, r2: IntRange) = r1.contains(r2.first) || r1.contains(r2.last)

private fun parse(input: List<String>): List<Pair<IntRange, IntRange>> {
    return input
        .map { it.split(",") }
        .map { parseRange(it[0]) to parseRange(it[1]) }
}

fun parseRange(s: String): IntRange {
    val edges = s.split("-").map { it.toInt() }
    return IntRange(edges[0], edges[1])
}
