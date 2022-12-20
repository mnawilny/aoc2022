package com.nawilny.aoc2022.day20

import com.nawilny.aoc2022.common.Input
import kotlin.math.abs

fun main() {
    val encryptedFile = Input.readFileLinesNormalized("day20", "input.txt").map { it.toInt() }
    getSolution(encryptedFile, 1, 1)
    getSolution(encryptedFile, 10, 811589153)
}

private fun getSolution(encryptedFile: List<Int>, iterations: Int, decryptionKey: Long): Long {
    val fileNumbers = convertToFileNumbers(encryptedFile.map { it * decryptionKey })
    val zero = fileNumbers.first { it.value == 0L }

    for (i in 1..iterations) {
        fileNumbers.forEach { it.move(fileNumbers.size) }
    }

    val e1 = zero.jump1000()
    val e2 = e1.jump1000()
    val e3 = e2.jump1000()
    val result = e1.value + e2.value + e3.value
    println("$e1 $e2 $e3 -> $result")
    return result
}

private data class FileNumber(val value: Long, var next: FileNumber?, var previous: FileNumber?) {
    fun move(cycleSize: Int) {
        previous!!.next = next!!
        next!!.previous = previous!!

        var newPosition = previous!!
        val op: (FileNumber) -> FileNumber? = if (value > 0) FileNumber::next else FileNumber::previous
        val count = abs(value) % (cycleSize - 1)
        for (i in 1..count) {
            newPosition = op(newPosition)!!
        }
        val newNext = newPosition.next!!
        this.previous = newPosition
        this.next = newNext
        newPosition.next = this
        newNext.previous = this
    }

    fun jump1000(): FileNumber {
        var n = this
        for (i in 0 until 1000) {
            n = n.next!!
        }
        return n
    }

    override fun toString(): String {
        return value.toString()
    }
}

private fun convertToFileNumbers(numbers: List<Long>): List<FileNumber> {
    val fileNumbers = numbers.map { FileNumber(it, null, null) }
    fileNumbers.withIndex().forEach { n ->
        n.value.previous = if (n.index == 0) {
            fileNumbers.last()
        } else {
            fileNumbers[n.index - 1]
        }
        n.value.next = if (n.index == fileNumbers.size - 1) {
            fileNumbers.first()
        } else {
            fileNumbers[n.index + 1]
        }
    }
    return fileNumbers
}

private fun toList(firstElement: FileNumber): List<Long> {
    val result = mutableListOf(firstElement.value)
    var n = firstElement.next!!
    while (n != firstElement) {
        result.add(n.value)
        n = n.next!!
    }
    return result
}
