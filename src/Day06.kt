fun main() {

    fun findUniq(line: String, windowSize: Int): Int? {
        val window = ArrayDeque<Char>(5)

        for ((i, s) in line.withIndex()) {
            window.addLast(s)
            if (window.size == windowSize+1) {
                window.removeFirst()
            }
            if (window.size == windowSize) {
                if (window.toSet().size == windowSize) {
                    return i + 1
                }
            }
        }

        return null
    }

    fun part1(input: List<String>): Int? {
        return findUniq(input.last(), 4)
    }

    fun part2(input: List<String>): Int? {
        return findUniq(input.last(), 14)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 7)
    check(part2(testInput) == 19)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))

}
