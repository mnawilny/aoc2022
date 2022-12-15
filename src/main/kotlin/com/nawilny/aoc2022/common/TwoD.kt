package com.nawilny.aoc2022.common

import kotlin.math.max
import kotlin.math.min

data class Point(val x: Int, val y: Int) {
    fun manhattanDistanceTo(p :Point) :Int {
        return (max(x, p.x) - min(x, p.x)) + (max(y, p.y) - min(y, p.y))
    }
}

enum class Direction(val next: (Point) -> Point) {
    UP({ Point(it.x, it.y - 1) }),
    DOWN({ Point(it.x, it.y + 1) }),
    LEFT({ Point(it.x - 1, it.y) }),
    RIGHT({ Point(it.x + 1, it.y) })
}
