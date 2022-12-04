data class Interval(var start: Int, var end: Int)

fun parseInterval(s: String): Interval {
    val (start, end) = s.split('-')
    return Interval(start.toInt(), end.toInt())
}

fun isFullyOverlap(int1: Interval, int2: Interval): Boolean {
    return int1.start <= int2.start && int1.end >= int2.end
}

fun isOverlap(int1: Interval, int2: Interval): Boolean {
    return !(int2.end < int1.start || int2.start > int1.end)
}

fun main() {

    fun part1(input: List<String>): Int {
        var totalOverlaps = 0
        for (line in input) {
            val (int1, int2) = line.split(',')
            val parsedInt1 = parseInterval(int1)
            val parsedInt2 = parseInterval(int2)
            if (isFullyOverlap(parsedInt1, parsedInt2) || isFullyOverlap(parsedInt2, parsedInt1)) {
                totalOverlaps += 1
            }
        }

        return totalOverlaps
    }

    fun part2(input: List<String>): Int {
        var totalOverlaps = 0
        for (line in input) {
            val (int1, int2) = line.split(',')
            val parsedInt1 = parseInterval(int1)
            val parsedInt2 = parseInterval(int2)
            if (isOverlap(parsedInt1, parsedInt2)) {
                totalOverlaps += 1
            }
        }

        return totalOverlaps
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
