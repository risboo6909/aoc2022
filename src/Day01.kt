fun main() {
    fun part1(input: List<String>): Int {
        var maxSoFar: Int = -1
        var groupSum = 0

        for (line: String in input) {
            val parsedInt = line.toIntOrNull()
            if (parsedInt != null) {
                groupSum += parsedInt
            } else {
                // group separator
                if (groupSum > maxSoFar) {
                    maxSoFar = groupSum
                }
                groupSum = 0
            }
        }

        if (groupSum > maxSoFar) {
            maxSoFar = groupSum
        }

        return maxSoFar
    }

    fun part2(input: List<String>): Int {
        var groupSum = 0
        val groupSums = mutableListOf<Int>()

        for (line: String in input) {
            val parsedInt = line.toIntOrNull()
            if (parsedInt != null) {
                groupSum += parsedInt
            } else {
                // group separator
                groupSums.add(groupSum)
                groupSum = 0
            }
        }

        groupSums.add(groupSum)

        return groupSums.sorted().reversed().take(3).sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
