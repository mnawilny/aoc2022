package com.nawilny.aoc2022.day16

import com.nawilny.aoc2022.common.Input

fun main() {
    val valves = Input.readFileLinesNormalized("day16", "input.txt").map { parseValve(it) }.associateBy { it.name }
    val startValve = "AA"
    val opened = setOf(startValve)

    val startAll = System.currentTimeMillis()

    // part 1
//    var states = mapOf(State(startValve, opened) to 0)
//    (1..30).forEach { i ->
//        val start = System.currentTimeMillis()
//        states = generateNextStates(states, valves).takeTop(10000)
//        val end = System.currentTimeMillis()
//        println("$i completed - time: ${(end - start) / 1000.0} - ${states.size}")
//    }

    // part 2
    var states = mapOf(State2(startValve, startValve, opened) to 0)
    (1..26).forEach { i ->
        val start = System.currentTimeMillis()
        states = generateNextStates2(states, valves).takeTop(10000)
        val end = System.currentTimeMillis()
        println("$i completed - time: ${(end - start) / 1000.0} - ${states.size}")
    }

    val endAll = System.currentTimeMillis()
    println("time: " + (endAll - startAll) / 1000.0)
    println(states.maxOf { it.value })
}

private fun generateNextStates(states: Map<State, Int>, valves: Map<String, Valve>): Map<State, Int> {
    val newStates = mutableMapOf<State, Int>()
    states.forEach { state ->
        val newScore = state.value + state.key.opened.sumOf { valves[it]!!.flowRate }
        if (state.key.opened.size == valves.size) {
            newStates[state.key] = newScore
        } else {
            valves[state.key.current]!!.leadsTo
                .map { State(it, state.key.opened) }
                .forEach { newStates.putIfHigher(it, newScore) }
            if (!state.key.opened.contains(state.key.current)) {
                val newState = State(state.key.current, state.key.opened.plus(state.key.current))
                newStates.putIfHigher(newState, newScore)
            }
        }
    }
    return newStates
}

private fun generateNextStates2(states: Map<State2, Int>, valves: Map<String, Valve>): Map<State2, Int> {
    val newStates = mutableMapOf<State2, Int>()
    states.forEach { state ->
        val newScore = state.value + state.key.opened.sumOf { valves[it]!!.flowRate }
        if (state.key.opened.size == valves.size) {
            newStates[state.key] = newScore
        } else {
            // both open
            if (!state.key.opened.contains(state.key.current1) && !state.key.opened.contains(state.key.current2)) {
                val newState = State2(
                    state.key.current1,
                    state.key.current2,
                    state.key.opened.plus(state.key.current1).plus(state.key.current2)
                )
                newStates.putIfHigher(newState, newScore)
            }
            // current1 open
            if (!state.key.opened.contains(state.key.current1)) {
                val newOpened = state.key.opened.plus(state.key.current1)
                valves[state.key.current2]!!.leadsTo
                    .map { State2(state.key.current1, it, newOpened) }
                    .forEach { newStates.putIfHigher(it, newScore) }
            }
            // current2 open
            if (!state.key.opened.contains(state.key.current2)) {
                val newOpened = state.key.opened.plus(state.key.current2)
                valves[state.key.current1]!!.leadsTo
                    .map { State2(it, state.key.current2, newOpened) }
                    .forEach { newStates.putIfHigher(it, newScore) }
            }
            // none open
            valves[state.key.current1]!!.leadsTo.flatMap { newC1 ->
                valves[state.key.current2]!!.leadsTo.map { newC1 to it }
            }.map { State2(it.first, it.second, state.key.opened) }
                .forEach { newStates.putIfHigher(it, newScore) }
        }
    }
    return newStates
}

private fun <T> MutableMap<T, Int>.putIfHigher(key: T, value: Int) {
    if (!this.contains(key) || this[key]!! < value) {
        this[key] = value
    }
}

private fun <T> Map<T, Int>.takeTop(n: Int): Map<T, Int> {
    return this.toList().sortedByDescending { it.second }.take(n).toMap()
}

private data class Valve(val name: String, val flowRate: Int, val leadsTo: List<String>)

private data class State(val current: String, val opened: Set<String>)

private data class State2(val current1: String, val current2: String, val opened: Set<String>)

private fun parseValve(line: String): Valve {
    val name = line.substring(6..7)
    val flowRate = line.split(";")[0].substring(23).toInt()
    val leadsTo = line.split(";")[1].substring(23).split(",").map { it.trim() }
    return Valve(name, flowRate, leadsTo)
}
