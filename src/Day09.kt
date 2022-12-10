import kotlin.math.abs

enum class Direction {
    LEFT,
    RIGHT,
    UP,
    DOWN,
}

typealias Step = Pair<Direction, Int>
typealias Coords = Pair<Int, Int>

fun buildStep(dir: String, count: String): Step? {
    return when (dir) {
        "R" -> Step(Direction.RIGHT, count.toInt()-1)
        "L" -> Step(Direction.LEFT, count.toInt()-1)
        "U" -> Step(Direction.UP, count.toInt()-1)
        "D" -> Step(Direction.DOWN, count.toInt()-1)
        else -> null
    }
}

class Rope(tailSize: Int) {
    private var head: Coords = Pair(0, 0)
    private var tail: MutableList<Coords> = (0 until tailSize).map { Pair(0, 0) }.toMutableList()

    private fun updateTail(): Coords {

        fun inner(headX: Int, headY: Int, idx: Int): Coords {
            if (idx == tail.size) {
                return tail[tail.size-1]
            }

            var (tailX, tailY) = tail[idx]
            val deltaX = abs(tailX - headX)
            val deltaY = abs(tailY - headY)

            if (deltaX < 2 && deltaY < 2) {
                return tail[tail.size-1]
            }

            if (deltaX >= 1) {
                if (headX > tailX) {
                    tailX++
                } else {
                    tailX--
                }
            }
            if (deltaY >= 1) {
                if (headY > tailY) {
                    tailY++
                } else {
                    tailY--
                }
            }

            tail[idx] = Pair(tailX, tailY)
            return inner(tail[idx].first, tail[idx].second, idx+1)
        }

        return inner(head.first, head.second, 0)
    }

    fun updateHead(step: Step): Set<Coords> {
        val (direction, count) = step
        val tailPositions: MutableSet<Coords> = mutableSetOf()

        for (littleStep in (0..count)) {
            val (headX, headY) = head

            head = when (direction) {
                Direction.LEFT -> {
                    Pair(headX-1, headY)
                }

                Direction.RIGHT -> {
                    Pair(headX+1, headY)
                }

                Direction.UP -> {
                    Pair(headX, headY-1)
                }

                Direction.DOWN -> {
                    Pair(headX, headY+1)
                }
            }

            tailPositions.add(updateTail())
        }
        return tailPositions
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val rope = Rope(1)
        var tailPositions: Set<Coords> = mutableSetOf()

        for (line in input) {
            val (direction, count) = line.split(' ')
            tailPositions = tailPositions union rope.updateHead(buildStep(direction, count)!!)
        }
        return tailPositions.size
    }

    fun part2(input: List<String>): Int {
        val rope = Rope(9)
        var tailPositions: Set<Coords> = mutableSetOf()

        for (line in input) {
            val (direction, count) = line.split(' ')
            tailPositions = tailPositions union rope.updateHead(buildStep(direction, count)!!)
        }
        return tailPositions.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 1)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
