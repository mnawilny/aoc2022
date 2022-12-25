package com.nawilny.aoc2022.day25

import com.nawilny.aoc2022.common.Input

fun main() {
    val input = Input.readFileLinesNormalized("day25", "input.txt")
    println(decimalToSnafu(input.sumOf { snafuToDecimal(it) }))
}

private fun snafuToDecimal(s: String): Long {
    return s.reversed().withIndex().sumOf { pow5(it.index) * snafuDigitToDecimal(it.value) }
}

private fun decimalToSnafu(i: Long): String {
    var d = i
    var result = ""
    while (d > 0) {
        val s = (d + 2) / 5
        val remain = d - (s * 5)
        result += decimalDigitToSnafu(remain)
        d = s
    }
    return result.reversed()
}

private fun pow5(i: Int) = (0 until i).fold(1L) { acc, _ -> acc * 5 }

private fun snafuDigitToDecimal(c: Char): Long {
    return when (c) {
        '=' -> -2
        '-' -> -1
        '0' -> 0
        '1' -> 1
        '2' -> 2
        else -> error("Unsupported digit '$c'")
    }
}

private fun decimalDigitToSnafu(i: Long): Char {
    return when (i) {
        -2L -> '='
        -1L -> '-'
        0L -> '0'
        1L -> '1'
        2L -> '2'
        else -> error("Cannot convert to snafu digit '$i'")
    }
}
