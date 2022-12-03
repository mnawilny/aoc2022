package com.nawilny.aoc2022.day03

import com.nawilny.aoc2022.common.Input

fun main() {
    val rucksacks = Input.readFileLines("day03", "input.txt").map { it.trim() }.filter { it.isNotEmpty() }
    println(rucksacks
        .map { it.substring(0 until (it.length / 2)) to it.substring((it.length / 2) until it.length) }
        .map { findCommon(it.first, it.second) }
        .sumOf { getPriority(it) }
    )
    println(groupIntoThrees(rucksacks)
        .map { findCommon(it[0], it[1], it[2]) }
        .sumOf { getPriority(it) })
}

fun findCommon(s1: String, s2: String, s3: String? = null): Char {
    s1.forEach { c ->
        if (s2.contains(c) && (s3 == null || s3.contains(c))) {
            return c
        }
    }
    throw IllegalStateException("Common char not found")
}

fun groupIntoThrees(input: List<String>): List<List<String>> {
    var three = mutableListOf<String>()
    val result = mutableListOf<List<String>>()
    input.forEach { i ->
        three.add(i)
        if (three.size == 3) {
            result.add(three)
            three = mutableListOf()
        }
    }
    if (three.isNotEmpty()) {
        throw IllegalStateException("Input not divisible by 3")
    }
    return result
}

fun getPriority(c: Char): Int {
    return if (c.isLowerCase()) {
        c.code - 'a'.code + 1
    } else {
        c.code - 'A'.code + 27
    }
}

