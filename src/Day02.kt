fun main() {
    fun part1(input: List<String>): Int {
        val scores = mapOf("X" to 1, "Y" to 2, "Z" to 3)
        val outcomes = mapOf(
            "A X" to 3, "A Y" to 6, "A Z" to 0,
            "B X" to 0, "B Y" to 3, "B Z" to 6,
            "C X" to 6, "C Y" to 0, "C Z" to 3,
        )
        var totalScore = 0

        for (line in input) {
            val (_, snd) = line.split(" ")
            totalScore += outcomes[line]!! + scores[snd]!!
        }
        return totalScore
    }

    fun part2(input: List<String>): Int {
        val outcomes = mapOf("X" to 0, "Y" to 3, "Z" to 6)
        val scores = mapOf(
            "A X" to 3, "A Y" to 1, "A Z" to 2,
            "B X" to 1, "B Y" to 2, "B Z" to 3,
            "C X" to 2, "C Y" to 3, "C Z" to 1,
        )
        var totalScore = 0

        for (line in input) {
            val (_, desiredOutcome) = line.split(" ")
            totalScore += scores[line]!! + outcomes[desiredOutcome]!!
        }

        return totalScore
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
