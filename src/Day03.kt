import kotlin.system.exitProcess

fun main() {
    val priorities = ('a'..'z').zip((1..26)).toMap() +
                     ('A'..'Z').zip((27..52)).toMap()

    fun part1(input: List<String>): Int {
        var totalSum = 0

        for (line in input) {
            val halfLen = line.length / 2
            val head = line.take(halfLen).asIterable()
            val tail = line.substring(halfLen).asIterable().toSet()

            totalSum += priorities[head.intersect(tail).single()]!!
        }

        return totalSum
    }

    fun part2(input: List<String>): Int {
        var intermediate = emptySet<Char>()
        var totalSum = 0

        for ((i, line) in input.withIndex()) {
            if (i > 0 && i % 3 == 0) {
                totalSum += priorities[intermediate.single()]!!
                intermediate = emptySet()
            }

            intermediate = if (intermediate.isEmpty()) {
                intermediate.union(line.asIterable())
            } else {
                intermediate.intersect(line.asIterable().toSet())
            }
        }

        totalSum += priorities[intermediate.single()]!!

        return totalSum
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
