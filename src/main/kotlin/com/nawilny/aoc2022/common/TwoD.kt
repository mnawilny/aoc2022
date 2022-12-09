package com.nawilny.aoc2022.common

data class Point(val x: Int, val y: Int)

enum class Direction(val next: (Point) -> Point) {
    UP({ Point(it.x, it.y - 1) }),
    DOWN({ Point(it.x, it.y + 1) }),
    LEFT({ Point(it.x - 1, it.y) }),
    RIGHT({ Point(it.x + 1, it.y) })
}
