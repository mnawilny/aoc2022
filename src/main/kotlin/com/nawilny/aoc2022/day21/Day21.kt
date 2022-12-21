package com.nawilny.aoc2022.day21

import com.nawilny.aoc2022.common.Input

fun main() {
    val monkeys = Input.readFileLinesNormalized("day21", "input.txt").map { parse(it) }.associateBy { it.name }
    val root = monkeys["root"]!! as OperatorMonkey

    println(root.yell(monkeys))

    val newRoot = EqualityMonkey(root)
    val humanMonkeyName = "humn"
    val part2Monkeys = monkeys.plus(humanMonkeyName to Human(humanMonkeyName))
    val humanValue = newRoot.calculateWhatShouldBeYelledByHuman(part2Monkeys)
    newRoot.verify(part2Monkeys, humanValue)
    println(humanValue)
}

private sealed interface Monkey {
    val name: String
    fun yell(monkeys: Map<String, Monkey>, humanValue: Long? = null): Long
    fun dependsOnHuman(monkeys: Map<String, Monkey>): Boolean
    fun calculateWhatShouldBeYelledByHuman(monkeys: Map<String, Monkey>, expectedResult: Long): Long
}

// naming assumes A op B = C
private enum class Operator(
    val execute: (Long, Long) -> Long,
    val getA: (Long, Long) -> Long,
    val getB: (Long, Long) -> Long
) {
    ADD(
        { a: Long, b: Long -> a + b },
        { b: Long, c: Long -> c - b },
        { a: Long, c: Long -> c - a },
    ),
    SUB(
        { a: Long, b: Long -> a - b },
        { b: Long, c: Long -> b + c },
        { a: Long, c: Long -> a - c }
    ),
    MUL(
        { a: Long, b: Long -> a * b },
        { b: Long, c: Long -> c / b },
        { a: Long, c: Long -> c / a }
    ),
    DIV(
        { a: Long, b: Long -> a / b },
        { b: Long, c: Long -> b * c },
        { a: Long, c: Long -> a / c }
    )
}

private data class NumberMonkey(override val name: String, val value: Long) : Monkey {
    override fun yell(monkeys: Map<String, Monkey>, humanValue: Long?): Long = value
    override fun dependsOnHuman(monkeys: Map<String, Monkey>) = false
    override fun calculateWhatShouldBeYelledByHuman(monkeys: Map<String, Monkey>, expectedResult: Long): Long {
        error("Something went wrong")
    }
}

private data class OperatorMonkey(
    override val name: String,
    val m1: String, val m2: String, val op: Operator
) : Monkey {

    override fun yell(monkeys: Map<String, Monkey>, humanValue: Long?): Long {
        return op.execute(monkeys[m1]!!.yell(monkeys, humanValue), monkeys[m2]!!.yell(monkeys, humanValue))
    }

    override fun dependsOnHuman(monkeys: Map<String, Monkey>): Boolean {
        return monkeys[m1]!!.dependsOnHuman(monkeys) || monkeys[m2]!!.dependsOnHuman(monkeys)
    }

    override fun calculateWhatShouldBeYelledByHuman(monkeys: Map<String, Monkey>, expectedResult: Long): Long {
        val monkey1 = monkeys[m1]!!
        val monkey2 = monkeys[m2]!!

        return if (monkey1.dependsOnHuman(monkeys)) {
            val neededLeft = op.getA(monkey2.yell(monkeys), expectedResult)
            monkey1.calculateWhatShouldBeYelledByHuman(monkeys, neededLeft)
        } else {
            val neededRight = op.getB(monkey1.yell(monkeys), expectedResult)
            monkey2.calculateWhatShouldBeYelledByHuman(monkeys, neededRight)
        }
    }
}

private data class Human(override val name: String) : Monkey {
    override fun yell(monkeys: Map<String, Monkey>, humanValue: Long?) = humanValue ?: error("Number not defined")
    override fun dependsOnHuman(monkeys: Map<String, Monkey>) = true
    override fun calculateWhatShouldBeYelledByHuman(monkeys: Map<String, Monkey>, expectedResult: Long) = expectedResult
}

private data class EqualityMonkey(val m: OperatorMonkey) {

    fun verify(monkeys: Map<String, Monkey>, humanValue: Long): Boolean {
        val left = monkeys[m.m1]!!.yell(monkeys, humanValue)
        val right = monkeys[m.m2]!!.yell(monkeys, humanValue)
        println("$left == $right -> ${left == right}")
        return left == right
    }

    fun calculateWhatShouldBeYelledByHuman(monkeys: Map<String, Monkey>): Long {
        val m1 = monkeys[m.m1]!!
        val m2 = monkeys[m.m2]!!
        val left = if (m1.dependsOnHuman(monkeys)) m1 else m2
        val right = if (left == m1) m2 else m1
        val totalValue = right.yell(monkeys)
        return left.calculateWhatShouldBeYelledByHuman(monkeys, totalValue)
    }
}

private fun parse(line: String): Monkey {
    val parts = line.split(":")
    val name = parts[0]
    val valueParts = parts[1].trim().split(" ")
    return if (valueParts.size == 1) {
        NumberMonkey(name, valueParts[0].toLong())
    } else {
        OperatorMonkey(name, valueParts[0], valueParts[2], parseOperator(valueParts[1]))
    }
}

private fun parseOperator(op: String): Operator {
    return when (op) {
        "+" -> Operator.ADD
        "-" -> Operator.SUB
        "*" -> Operator.MUL
        "/" -> Operator.DIV
        else -> error("Invalid operator '$op'")
    }
}
