package com.nawilny.aoc2022.day07

import com.nawilny.aoc2022.common.Input

fun main() {
    val lines = Input.readFileLinesNormalized("day07", "input.txt")
    val root = parseInput(lines)
    root.print()

    // part 1
    println(root.getAllDirs().filter { it.size <= 100000 }.sumOf { it.size })

    // part 2
    val freeSpace = 70000000 - root.size
    val missingSpace = 30000000 - freeSpace
    val dirToRemove = root.getAllDirs().filter { it.size >= missingSpace }.minByOrNull { it.size }!!
    println("${dirToRemove.name} - ${dirToRemove.size}")
}

private fun parseInput(input: List<String>): Directory {
    val root = Directory("/", null)
    var current = root

    input.map { it.split(" ") }.forEach { line ->
        if (line[0] == "$") {
            if (line[1] == "cd" && line[2] == "/") {
                current = root
            } else if (line[1] == "cd" && line[2] == "..") {
                current = current.cdUp()
            } else if (line[1] == "cd") {
                current = current.cd(line[2])
            }
            // ignoring other commands (like ls)
        } else {
            if (line[0] == "dir") {
                current.add(Directory(line[1], current))
            } else {
                current.add(File(line[1], line[0].toInt()))
            }
        }
    }
    return root
}

private sealed interface FileSystemItem {
    val name: String
    val size: Int
    fun print(indent: Int = 0)
}

private data class Directory(override val name: String, private val parent: Directory?) : FileSystemItem {

    override val size: Int
        get() = content.values.sumOf { it.size }

    private val content = mutableMapOf<String, FileSystemItem>()

    fun add(item: FileSystemItem) {
        content[item.name] = item
    }

    fun cd(name: String): Directory {
        val d = content[name]
        if (d == null || d !is Directory) throw error("'$name' not found or not directory")
        return d
    }

    fun cdUp() = parent ?: error("No parent")

    override fun print(indent: Int) {
        printIndent(indent)
        println("- $name <dir> ($size)")
        content.values.sortedBy { it.name }.forEach { it.print(indent + 1) }
    }

    fun getAllDirs(): List<Directory> {
        val dirs = content.values.filterIsInstance<Directory>()
        return dirs.plus(dirs.flatMap { it.getAllDirs() })
    }

}

private data class File(override val name: String, override val size: Int) : FileSystemItem {
    override fun print(indent: Int) {
        printIndent(indent)
        println("- $name ($size)")
    }
}

private fun printIndent(indent: Int) {
    (0..indent).forEach { _ -> print("  ") }
}
