import java.util.PriorityQueue
import kotlin.streams.toList
import kotlin.math.abs

const val START = 83
const val END = 69

fun Vector2.manhattanDist(b: Vector2): Int {
    return abs(this.first-b.first) + abs(this.second-b.second)
}

fun main() {
    fun readMap(input: List<String>): List<MutableList<Int>> {
        return input.map { it.chars().toList().toMutableList() }
    }

    fun findOnMap(field: List<List<Int>>, needle: Int): Vector2? {
        for ((rowIdx, row) in field.withIndex()) {
            val colIdx = row.indexOf(needle)
            if (colIdx != -1) {
                return Pair(rowIdx, colIdx)
            }
        }
        return null
    }

    fun getNeighbours(field: List<List<Int>>, pos: Vector2): MutableList<Vector2> {
        val res: MutableList<Vector2> = mutableListOf()
        val (rowIdx, colIdx) = pos

        if (rowIdx+1 < field.size && field[rowIdx][colIdx]+1 >= field[rowIdx+1][colIdx]) {
            res.add(Vector2(rowIdx+1, colIdx))
        }
        if (rowIdx-1 >= 0 && field[rowIdx][colIdx]+1 >= field[rowIdx-1][colIdx]) {
            res.add(Vector2(rowIdx-1, colIdx))
        }
        if (colIdx+1 < field[0].size && field[rowIdx][colIdx]+1 >= field[rowIdx][colIdx+1]) {
            res.add(Vector2(rowIdx, colIdx+1))
        }
        if (colIdx-1 >= 0 && field[rowIdx][colIdx]+1 >= field[rowIdx][colIdx-1]) {
            res.add(Vector2(rowIdx, colIdx-1))
        }

        return res
    }

    fun computeDistance(paths: Map<Vector2, Vector2>, startPos: Vector2, endPos: Vector2): Int {
        var curNode = endPos
        var totalNodes = 0

        while (curNode != startPos) {
            if (!paths.containsKey(curNode)) {
                return Int.MAX_VALUE
            }
            curNode = paths[curNode]!!
            totalNodes++
        }
        return totalNodes
    }

    fun shortestAStar(field: List<MutableList<Int>>, startPos: Vector2, endPos: Vector2, weights: MutableMap<Vector2, Int>): MutableMap<Vector2, Vector2> {
        field[startPos.first][startPos.second] = 'a'.code
        field[endPos.first][endPos.second] = 'z'.code

        weights[startPos] = 0

        val paths: MutableMap<Vector2, Vector2> = mutableMapOf()

        val frontier = PriorityQueue<Pair<Vector2, Int>>{ a, b -> a.second - b.second}
        frontier.add(Pair(startPos, 0))

        while (frontier.isNotEmpty()) {
            val (curPos, _) = frontier.poll()
            if (curPos == endPos) {
                break
            }
            for (nextPos in getNeighbours(field, curPos)) {
                val newWeight = weights[curPos]!! + 1
                if (!weights.containsKey(nextPos) || weights[nextPos]!! > newWeight) {
                    weights[nextPos] = newWeight
                    paths[nextPos] = curPos
                    frontier.add(Pair(nextPos, newWeight + nextPos.manhattanDist(endPos)))
                }
            }

        }

        return paths
    }

    fun part1(input: List<String>): Int {
        val field = readMap(input)
        val startPos = findOnMap(field, START)!!
        val endPos = findOnMap(field, END)!!
        val weights: MutableMap<Vector2, Int> = mutableMapOf(Pair(startPos, 0))

        val paths = shortestAStar(field, startPos, endPos, weights)
        return computeDistance(paths, startPos, endPos)
    }

    fun part2(input: List<String>): Int {
        val field = readMap(input)
        val endPos = findOnMap(field, END)!!
        val weights: MutableMap<Vector2, Int> = mutableMapOf()

        return field.withIndex().flatMap { (rowIdx, row) ->
            row.withIndex().map { (colIdx, e) ->
                if (e == 'a'.code) {
                    val startPos = Vector2(rowIdx, colIdx)
                    val paths = shortestAStar(field, startPos, endPos, weights)
                    computeDistance(paths, startPos, endPos)
                } else {
                    Int.MAX_VALUE
                }
            }
        }.min()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}
