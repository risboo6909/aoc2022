data class Scanner(val center: Coords, val beacon: Coords)

fun main() {

    val matcher = ".+?x=(?<x1>-?\\d+), y=(?<y1>-?\\d+).+x=(?<x2>-?\\d+), y=(?<y2>-?\\d+)".toRegex()

    fun parse(input: List<String>): List<Scanner> {
        val res = emptyList<Scanner>().toMutableList()
        for (line in input) {
            val groups = matcher.matchEntire(line)!!.groups
            val x1 = groups["x1"]!!.value.toInt()
            val y1 = groups["y1"]!!.value.toInt()
            val x2 = groups["x2"]!!.value.toInt()
            val y2 = groups["y2"]!!.value.toInt()
            res.add(Scanner(Coords(x1, y1), Coords(x2, y2)))
        }
        return res
    }

    fun scanLine(scanners: List<Scanner>, targetY: Int): Pair<Set<Coords>, Set<Int>> {
        val segments = mutableSetOf<Coords>()
        val exclude = scanners.
                filter { it.beacon.second == targetY }.
                map { it.beacon.first }.toSet()

        for (scanner in scanners) {
            val dist = scanner.center.manhattanDist(scanner.beacon)

            val tmp = if (scanner.center.second < targetY) {
                (dist - targetY + scanner.center.second) shl 1
            } else {
                (dist + targetY - scanner.center.second) shl 1
            }

            if (kotlin.math.abs(targetY - scanner.center.second) <= dist) {
                val deltaX = (tmp shr 1)

                val stX = scanner.center.first - deltaX
                val endX = scanner.center.first + deltaX

                segments.add(Coords(stX, endX))
            }
        }

        return Pair(segments, exclude)
    }

    fun part1(input: List<String>, targetY: Int): Int {
        val res = scanLine(parse(input), targetY)
        val segments = res.first.sortedBy { it.first }

        return segments.last().second - segments.first().first - res.second.size + 1
    }

    fun findGap(segments: Set<Coords>): Int? {
        var maxSoFar = Int.MIN_VALUE
        for ((cur, next) in segments.sortedBy { it.first }.zipWithNext()) {
            if (cur.second > maxSoFar) {
                maxSoFar = cur.second
            }
            if (next.first > maxSoFar) {
                return cur.second + 1
            }
        }
        return null
    }

    fun part2(input: List<String>, maxCoord: Int): Long? {
        val parsed = parse(input)
        return (0..maxCoord).map{
            val res = scanLine(parsed, it)
            val gapX = findGap(res.first)

            if (gapX != null && !res.second.contains(gapX))
                4_000_000.toLong() * gapX + it
            else
                null
        }.firstNotNullOf { it }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput, 10) == 26)
    check(part2(testInput, 20) == 56000011.toLong())

    val input = readInput("Day15")
    println(part1(input, 2_000_000))
    println(part2(input, 4_000_000))
}
