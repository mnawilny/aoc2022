package com.nawilny.aoc2022.day11

fun main() {
    // example
//    val monkeys = listOf(
//        Monkey(mutableListOf(79, 98), { it * 19 }, 23, 2, 3),
//        Monkey(mutableListOf(54, 65, 75, 74), { it + 6 }, 19, 2, 0),
//        Monkey(mutableListOf(79, 60, 97), { it * it }, 13, 1, 3),
//        Monkey(mutableListOf(74), { it + 3 }, 17, 0, 1)
//    )
    // input
    val monkeys = listOf(
        Monkey(mutableListOf(54, 53), { it * 3 }, 2, 2, 6),
        Monkey(mutableListOf(95, 88, 75, 81, 91, 67, 65, 84), { it * 11 }, 7, 3, 4),
        Monkey(mutableListOf(76, 81, 50, 93, 96, 81, 83), { it + 6 }, 3, 5, 1),
        Monkey(mutableListOf(83, 85, 85, 63), { it + 4 }, 11, 7, 4),
        Monkey(mutableListOf(85, 52, 64), { it + 8 }, 17, 0, 7),
        Monkey(mutableListOf(57), { it + 2 }, 5, 1, 3),
        Monkey(mutableListOf(60, 95, 76, 66, 91), { it * it }, 13, 2, 5),
        Monkey(mutableListOf(65, 84, 76, 72, 79, 65), { it + 5 }, 19, 6, 0)
    )

    (1..10000).forEach { i ->
        round(monkeys, decreaseWorryLevel = false)
//        println("$i: " + monkeys.map { it.items })
    }
    println(monkeys.map { it.inspectedItems })
    println(monkeys.map { it.inspectedItems }.sortedDescending().take(2).multiply())

}

private fun round(monkeys: List<Monkey>, decreaseWorryLevel: Boolean) {
    val limit = monkeys.map { it.testDivisibleBy }.multiply()
    monkeys.forEach { it.inspectAndPassItems(monkeys, limit, decreaseWorryLevel) }
}

private class Monkey(
    val items: MutableList<Long>,
    val operation: (Long) -> Long,
    val testDivisibleBy: Long,
    val ifTrueThrowTo: Int,
    val ifFalseThrowTo: Int
) {
    var inspectedItems = 0L

    fun inspectAndPassItems(monkeys: List<Monkey>, limit: Long, decreaseWorryLevel: Boolean) {
        inspectedItems += items.size
        items.map { operation(it) }
            .map { if (decreaseWorryLevel) it / 3 else it }
            .map { it % limit }
            .map { it to if (it % testDivisibleBy == 0L) monkeys[ifTrueThrowTo] else monkeys[ifFalseThrowTo] }
            .forEach { it.second.pass(it.first) }
        items.clear()
    }

    fun pass(item: Long) {
        items.add(item)
    }
}

private fun List<Long>.multiply() = this.fold(1L) { acc, i -> acc * i }
