package com.nawilny.aoc2022.day10

import com.nawilny.aoc2022.common.Input

fun main() {
    val lines = Input.readFileLinesNormalized("day10", "input.txt")

    val crt = CRT()
    val cpu = CPU(crt)
    lines.forEach { cpu.execute(it) }

    println(cpu.getPart1Solution())
    crt.print()
}

private class CPU(private val crt: CRT) {
    private var tick = 0
    private var x = 1

    private val solution1 = mutableMapOf<Int, Int>()

    fun execute(cmd: String) {
        when {
            cmd == "noop" -> tick()
            cmd.startsWith("addx ") -> {
                tick()
                tick()
                x += cmd.substring(5).toInt()
            }
            else -> error("Unknown command '$cmd'")
        }
    }

    private fun tick() {
        crt.markPixel(tick, x)
        tick++
        if ((tick + 20) % 40 == 0) {
            solution1[tick] = x
        }
    }

    fun getPart1Solution() = solution1.map { it.key * it.value }.sum()
}

private class CRT {

    private val pixels = mutableListOf<Boolean>()

    fun markPixel(tick: Int, x: Int) {
        val rowPosition = tick % 40
        val isInSprite = rowPosition >= x - 1 && rowPosition <= x + 1
        pixels.add(isInSprite)
    }

    fun print() {
        pixels.map { if (it) '#' else '.' }.withIndex().forEach {
            if (it.index % 40 == 0) {
                println()
            }
            print(it.value)
        }
    }
}
