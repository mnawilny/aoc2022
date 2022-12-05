package com.nawilny.aoc2022.common

object Input {

    fun readFileLines(day: String, fileName: String): List<String> {
        val path = "$day/$fileName"
        return this::class.java.classLoader.getResourceAsStream(path)?.bufferedReader()?.readLines()
            ?: throw NullPointerException("File $path not found")
    }

    fun readFileLinesNormalized(day: String, fileName: String): List<String> {
        return readFileLines(day, fileName).map { it.trim() }.filter { it.isNotEmpty() }
    }

    fun divideByNewLines(input: List<String>): List<List<String>> {
        val result = mutableListOf<List<String>>()
        var section = mutableListOf<String>()
        input.forEach {
            if (it.isNotBlank()) {
                section.add(it)
            } else if (section.isNotEmpty()) {
                result.add(section)
                section = mutableListOf()
            }
        }
        if (section.isNotEmpty()) {
            result.add(section)
        }
        return result
    }

}
