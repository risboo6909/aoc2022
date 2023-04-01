enum class ResType {
    GEODE,
    OBSIDIAN,
    CLAY,
    ORE,
}

typealias NumItem = Map<ResType, Int>
typealias Blueprint = Map<ResType, NumItem>

fun main() {

    fun matchRes(res: String): ResType? {
        return when (res) {
            "ore" -> ResType.ORE
            "clay" -> ResType.CLAY
            "obsidian" -> ResType.OBSIDIAN
            "geode" -> ResType.GEODE
            else -> null
        }
    }

    fun parse(input: List<String>): List<Blueprint> {
        val res = mutableListOf<Blueprint>()
        val curBlueprint = mutableMapOf<ResType, NumItem>()

        for (line in input) {
            curBlueprint.clear()
            val blueprint = line.split(":").last().split(".")

            for (robot in blueprint.filterNot(String::isEmpty)) {
                val xs = robot.trim().split(" ")
                curBlueprint[matchRes(xs[1])!!] =
                    xs.subList(4, xs.size).zipWithNext().filter { (a, b) -> a != "and" && b != "and" }
                        .associate { (a, b) -> Pair(matchRes(b)!!, a.toInt()) }
            }

            res.add(HashMap(curBlueprint))
        }

        return res
    }

    fun canProduce(have: Map<ResType, Int>, required: NumItem): Pair<Boolean, MutableMap<ResType, Int>> {
        val resLeft = HashMap(have)
        for ((reqRes, reqCount) in required) {
            if (have.containsKey(reqRes)) {
                if (have[reqRes]!! < reqCount) {
                    return Pair(false, resLeft)
                } else {
                    resLeft[reqRes] = have[reqRes]!! - reqCount
                }
            } else {
                return Pair(false, resLeft)
            }
        }
        return Pair(true, resLeft)
    }

    fun compute(blueprint: Blueprint, totalMinutes: Int): Int {

        val table = HashMap<String, Int>()
        val maxOre = blueprint.values.maxOfOrNull { it[ResType.ORE]!! }!!

        fun inner(curRes: MutableMap<ResType, Int>, curRobots: MutableMap<ResType, Int>, newRobot: ResType?, minute: Int): Int {

            var maxGeodes = 0

            // produce resources
            for ((robotType, robotCount) in curRobots) {
                curRes[robotType] = curRes.getOrDefault(robotType, 0) + robotCount
            }

            if (minute >= totalMinutes) {
                return curRes.getOrDefault(ResType.GEODE, 0)
            }

            val minutesLeft = totalMinutes - minute

            // no need to build more robots than resource of the type required
            curRobots[ResType.ORE] = minOf(curRobots.getOrDefault(ResType.ORE, 0), maxOre)
            curRobots[ResType.CLAY] = minOf(
                curRobots.getOrDefault(ResType.CLAY, 0),
                blueprint[ResType.OBSIDIAN]!![ResType.CLAY]!!
            )
            curRobots[ResType.OBSIDIAN] = minOf(
                curRobots.getOrDefault(ResType.OBSIDIAN, 0),
                blueprint[ResType.GEODE]!![ResType.OBSIDIAN]!!
            )

            // restrain max resources
            curRes[ResType.ORE] =  minOf(curRes.getOrDefault(ResType.ORE, 0),
                (minutesLeft * maxOre) - curRobots[ResType.ORE]!! * (minutesLeft - 1))
            curRes[ResType.CLAY] = minOf(curRes.getOrDefault(ResType.CLAY, 0),
                (minutesLeft * blueprint[ResType.OBSIDIAN]!![ResType.CLAY]!!) -
                        curRobots[ResType.CLAY]!! * (minutesLeft - 1))
            curRes[ResType.OBSIDIAN] = minOf(curRes.getOrDefault(ResType.OBSIDIAN, 0),
                (minutesLeft * blueprint[ResType.GEODE]!![ResType.OBSIDIAN]!!) -
                        curRobots[ResType.OBSIDIAN]!! * (minutesLeft - 1))

            var key = "$curRes$curRobots$newRobot$minute"
            if (table.containsKey(key)) {
                return table[key]!!
            }

            // produce new robot
            if (newRobot != null) {
                curRobots[newRobot] = curRobots.getOrDefault(newRobot, 0) + 1
            }

            var robotTypes = ResType.values()
            if (minutesLeft <= 4) {
                robotTypes = arrayOf(ResType.GEODE)
            } else if (minutesLeft <= 7) {
                robotTypes = arrayOf(ResType.GEODE, ResType.OBSIDIAN)
            } else if (minutesLeft <= 15) {
                robotTypes = arrayOf(ResType.GEODE, ResType.OBSIDIAN, ResType.CLAY)
            }

            var lastProducedRobot: ResType? = null

            // see what is it possible to produce at the moment
            for (robotType in robotTypes) {

                val requiredRes = blueprint[robotType]!!

                val (ok, resLeft) = canProduce(curRes, requiredRes)
                if (!ok)
                    continue

                lastProducedRobot = robotType

                maxGeodes =
                    maxOf(maxGeodes, inner(HashMap(resLeft), HashMap(curRobots), robotType, minute + 1))

                if (robotType == ResType.GEODE)
                    break
            }

            if (lastProducedRobot != ResType.GEODE) {
                maxGeodes = maxOf(maxGeodes, inner(HashMap(curRes), HashMap(curRobots), null, minute + 1))
            }

            table[key] = maxGeodes

            return maxGeodes
        }

        return inner(mutableMapOf(), mutableMapOf(ResType.ORE to 1), null, 1)
    }

    fun part1(input: List<String>): Int {
        return parse(input)
            .withIndex()
            .sumOf {
                    (i, blueprint) -> compute(blueprint, 24) * (i + 1)
            }
    }

    fun part2(input: List<String>): Int {
        val parsed = parse(input)

        return parsed.subList(0, minOf(parsed.size, 3))
            .parallelStream()
            .map{bp -> compute(bp, 32)}
            .reduce { acc, i -> acc * i }
            .get()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 33)
    check(part2(testInput) == 3472)

    val input = readInput("Day19")
    println(part1(input))
    println(part2(input))
}
