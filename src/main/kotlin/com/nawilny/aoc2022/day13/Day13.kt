package com.nawilny.aoc2022.day13

import com.nawilny.aoc2022.common.Input

fun main() {
    val input = Input.readFileLines("day13", "input.txt")
    val part1 = Input.divideByNewLines(input).withIndex().filter {
        val first = parse(it.value[0])
        val second = parse(it.value[1])
        first.compareTo(second)!!
    }.sumOf { it.index + 1 }
    println(part1)

    val all = input.filter { it.isNotEmpty() }.map { parse(it) }

    val dp1 = parse("[[2]]")
    val dp2 = parse("[[6]]")
    val s = all.plusElement(dp1).plusElement(dp2).sortedWith { i1, i2 -> if (i1.compareTo(i2)!!) -1 else 1 }
//    s.forEach { println(it) }
    println((s.indexOf(dp1) + 1) * (s.indexOf(dp2) + 1))
}

private sealed interface Item {
    fun compareTo(secondItem: Item): Boolean?
}

private data class ListItem(val items: List<Item>) : Item {
    override fun compareTo(secondItem: Item): Boolean? {
        if (secondItem is ListItem) {
            items.indices.forEach { i ->
                if (i >= secondItem.items.size) {
                    return false
                }
                val c = items[i].compareTo(secondItem.items[i])
                if (c != null) {
                    return c
                }
            }
            return if (secondItem.items.size > items.size) {
                true
            } else {
                null
            }
        } else {
            return this.compareTo((secondItem as IntItem).toList())
        }
    }

    override fun toString(): String {
        return items.toString()
    }
}

private data class IntItem(val value: Int) : Item {
    override fun compareTo(secondItem: Item): Boolean? {
        return if (secondItem is IntItem) {
            when {
                secondItem.value > value -> true
                secondItem.value < value -> false
                else -> null
            }
        } else {
            toList().compareTo(secondItem)
        }
    }

    fun toList() = ListItem(listOf(this))

    override fun toString(): String {
        return value.toString()
    }
}

private fun parse(input: String): Item {
    return if (input.startsWith("[")) {
        var p = 0
        var current = ""
        val items = mutableListOf<String>()
        input.forEach { c ->
            when {
                c == '[' -> {
                    if (p > 0) current += c
                    p++
                }
                c == ']' -> {
                    if (p > 1) current += c
                    p--
                }
                c == ',' && p == 1 -> {
                    items.add(current)
                    current = ""
                }
                else -> current += c
            }
        }
        if (current.isNotEmpty()) items.add(current)
        ListItem(items.map { parse(it) })
    } else {
        IntItem(input.toInt())
    }
}
