package com.nawilny.aoc2022.day19

import com.nawilny.aoc2022.common.Input

fun main() {
    val blueprints = Input.readFileLinesNormalized("day19", "input.txt").map { parseBlueprint(it) }

//    val totalTime = 24
//    val testedBlueprints = blueprints

    val totalTime = 32
    val testedBlueprints = blueprints.take(3)

    val start = System.currentTimeMillis()

    val bestResults = testedBlueprints.map { blueprint ->
        val robots = Resources(1, 0, 0, 0)
        val resources = Resources(0, 0, 0, 0)
        val result = ResourceType.values().map {
            simulate(it, totalTime, resources, robots, blueprint)
        }.maxByOrNull { it.resources.geode }!!
        println("${blueprint.id}: ${result.resources.geode} - ${result.path.reversed()}")
        blueprint.id to result.resources.geode
    }

    val end = System.currentTimeMillis()
    println("time: " + (end - start) / 1000.0)
    println("part 1 " + bestResults.sumOf { it.first * it.second })
    println("part 2 " + bestResults.map { it.second }.multiply())
}

private fun List<Int>.multiply() = this.fold(1L) { acc, i -> acc * i }

private fun simulate(
    nextRobot: ResourceType?,
    remainingTime: Int,
    resources: Resources,
    robots: Resources,
    blueprint: Blueprint
): SimulationResult {
    var currentTime = remainingTime
    var currentResources = resources
    val robotCost = if (nextRobot == null) null else blueprint.robotCosts[nextRobot]!!
    while (currentTime > 0) {
        if (robotCost != null && currentResources.canAfford(robotCost)) {
            currentResources = currentResources.subtract(robotCost)
            currentTime--
            currentResources = currentResources.add(robots)
            val currentRobots = robots.add(nextRobot!!)
            val r = ResourceType.values()
                .filter { currentRobots.get(it) < blueprint.maxProduction[it]!! }
                .map { it to simulate(it, currentTime, currentResources, currentRobots, blueprint) }
                .maxByOrNull { it.second.resources.geode }
            return if (r == null) {
                simulate(null, currentTime, currentResources, currentRobots, blueprint)
            } else {
                SimulationResult(r.second.resources, r.second.path.plus(r.first))
            }
        } else {
            currentResources = currentResources.add(robots)
            currentTime--
        }
    }
    return SimulationResult(currentResources, listOf())
}

private data class SimulationResult(val resources: Resources, val path: List<ResourceType>)

private data class Blueprint(val id: Int, val robotCosts: Map<ResourceType, Resources>) {
    val maxProduction = mapOf(
        ResourceType.ORE to robotCosts.values.maxOf { it.ore },
        ResourceType.CLAY to robotCosts.values.maxOf { it.clay },
        ResourceType.OBSIDIAN to robotCosts.values.maxOf { it.obsidian },
        ResourceType.GEODE to Int.MAX_VALUE
    )
}

private enum class ResourceType {
    ORE, CLAY, OBSIDIAN, GEODE
}

private data class Resources(val ore: Int, val clay: Int, val obsidian: Int, val geode: Int) {

    fun add(r: Resources) = Resources(ore + r.ore, clay + r.clay, obsidian + r.obsidian, geode + r.geode)

    fun subtract(r: Resources) = Resources(ore - r.ore, clay - r.clay, obsidian - r.obsidian, geode - r.geode)

    fun canAfford(r: Resources) = ore >= r.ore && clay >= r.clay && obsidian >= r.obsidian && geode >= r.geode

    fun add(t: ResourceType) = when (t) {
        ResourceType.ORE -> Resources(ore + 1, clay, obsidian, geode)
        ResourceType.CLAY -> Resources(ore, clay + 1, obsidian, geode)
        ResourceType.OBSIDIAN -> Resources(ore, clay, obsidian + 1, geode)
        ResourceType.GEODE -> Resources(ore, clay, obsidian, geode + 1)
    }

    fun get(t: ResourceType) = when (t) {
        ResourceType.ORE -> ore
        ResourceType.CLAY -> clay
        ResourceType.OBSIDIAN -> obsidian
        ResourceType.GEODE -> geode
    }
}

private fun parseBlueprint(s: String): Blueprint {
    val parts = s.split(" ")
    return Blueprint(
        id = parts[1].substring(0, parts[1].length - 1).toInt(),
        robotCosts = mapOf(
            ResourceType.ORE to Resources(parts[6].toInt(), 0, 0, 0),
            ResourceType.CLAY to Resources(parts[12].toInt(), 0, 0, 0),
            ResourceType.OBSIDIAN to Resources(parts[18].toInt(), parts[21].toInt(), 0, 0),
            ResourceType.GEODE to Resources(parts[27].toInt(), 0, parts[30].toInt(), 0)
        )
    )
}
