data class Room(val rate: Int, val dest: List<String>)

fun main() {

    val leftMatcher = "Valve\\s(?<valve>\\w{2}).+rate=(?<rate>\\d+)".toRegex()

    fun parse(input: List<String>): Map<String, Room> {
        val tunnelsMap = mutableMapOf<String, Room>()
        for (line in input) {
            val (left, right) = line.split(';')
            val groups = leftMatcher.matchEntire(left)!!.groups

            val valve = groups["valve"]!!.value
            val rate = groups["rate"]!!.value.toInt()

            val tmp = right.removePrefix(" tunnels lead to valves ").
                            removePrefix(" tunnel leads to valve ")

            tunnelsMap[valve] = Room(rate, tmp.split(", "))
        }
        return tunnelsMap
    }

    fun traverse(tunnels: Map<String, Room>, curValve: String, minutesLeft: Int, initialStates: Set<String>): Map<Set<String>, Int> {

        // <Room, Time left> -> best score
        val cache = mutableMapOf<Triple<String, Set<String>, Int>, Int>()
        // Room -> time from valve on
        val states = mutableMapOf<String, Boolean>()

        val stateScore = mutableMapOf<Set<String>, Int>()

        for (valve in tunnels.keys) {
            if (tunnels[valve]!!.rate > 0) {
                states[valve] = false
            }
            if (initialStates.contains(valve)) {
                states[valve] = true
            }
        }

        fun inner(curValve: String, minutesLeft: Int, scoreSoFar: Int, openedValves: Set<String>) {

            if (stateScore.getOrDefault(openedValves, -1) < scoreSoFar) {
                stateScore[openedValves] = scoreSoFar
            }

            val cacheKey = Triple(curValve, openedValves, minutesLeft)
            if (cache.getOrDefault(cacheKey, -1) >= scoreSoFar) {
                return
            }
            cache[cacheKey] = scoreSoFar

            if (minutesLeft < 2 || openedValves.size == states.size) {
                return
            }

            val rate = tunnels[curValve]!!.rate
            for (dest in tunnels[curValve]!!.dest) {
                // if valve is already turned on, it is meaningless to turn it off again
                if (rate > 0 && !states[curValve]!!) {
                    states[curValve] = true
                    inner(
                        dest,
                        minutesLeft - 2,
                        scoreSoFar + (minutesLeft - 1) * rate,
                         states.filterValues { it }.keys
                    )
                    states[curValve] = false
                }
                inner(dest, minutesLeft - 1, scoreSoFar, openedValves)
            }
        }

        inner(curValve, minutesLeft, 0, states.filterValues { it }.keys)
        return stateScore
    }

    fun part1(input: List<String>): Int {
        return traverse(parse(input), "AA", 30, emptySet()).values.max()
    }

    fun part2(input: List<String>): Int {
        val minutesLeft = 26

        val parsed = parse(input)
        val openedValves = traverse(parsed, "AA", minutesLeft, emptySet())

        return openedValves.toList().parallelStream().map{
            (opened, value) -> traverse(parsed, "AA", minutesLeft, opened).values.max() + value
        }.toList().max()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 1651)
    check(part2(testInput) == 1707)

    val input = readInput("Day16")
    println(part1(input))
    println(part2(input))
}
