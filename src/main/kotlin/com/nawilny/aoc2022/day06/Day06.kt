package com.nawilny.aoc2022.day06

import com.nawilny.aoc2022.common.Input

fun main() {
    println(findMarker("bvwbjplbgvbhsrlpgdmjqwftvncz", 4)) // 5
    println(findMarker("nppdvjthqldpwncqszvftbrmjlhg", 4)) // 6
    println(findMarker("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 4)) // 10
    println(findMarker("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 4)) // 11
    println(findMarker("mjqjpqmgbljsphdztnvjfqwrcgsmlb", 14)) // 19
    println(findMarker("bvwbjplbgvbhsrlpgdmjqwftvncz", 14)) // 23
    println(findMarker("nppdvjthqldpwncqszvftbrmjlhg", 14)) // 23
    println(findMarker("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 14)) // 29
    println(findMarker("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 14)) // 26

    println("---")
    val line = Input.readFileLines("day06", "input.txt").first()
    println(findMarker(line, 4))
    println(findMarker(line, 14))
}

private fun findMarker(line: String, length: Int): Int? {
    val marker = CharCounter()
    line.substring(0 until length).forEach { marker.put(it) }

    for (i in length until line.length) {
        marker.remove(line[i - length])
        marker.put(line[i])
        if (marker.allUnique()) {
            return i + 1
        }
    }
    return null
}

private class CharCounter {

    val buffer = mutableMapOf<Char, Int>()

    fun put(c: Char) {
        buffer[c] = buffer.getOrDefault(c, 0) + 1
    }

    fun remove(c: Char) {
        buffer[c] = buffer.getOrDefault(c, 0) - 1
    }

    fun allUnique() = buffer.values.all { it < 2 }

}
