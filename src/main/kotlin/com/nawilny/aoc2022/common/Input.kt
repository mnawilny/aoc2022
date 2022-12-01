package com.nawilny.aoc2022.common

import java.lang.NullPointerException

object Input {

    fun readFileLines(day: String, fileName: String): List<String> {
        val path = "$day/$fileName"
        return this::class.java.classLoader.getResourceAsStream(path)?.bufferedReader()?.readLines()
                ?: throw NullPointerException("File $path not found")
    }

}
