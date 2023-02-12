enum class Entity {
    ROCK,
    BALL,
}

private infix fun Int.toward(to: Int): IntProgression {
    val step = if (this > to) -1 else 1
    return IntProgression.fromClosedRange(this, to, step)
}

fun main() {
    val source = Vector2(500, 0)

    fun makeObstacles(input: List<String>): MutableMap<Vector2, Entity> {
        val res = mutableMapOf<Vector2, Entity>()
        for (line in input) {
            val tmp = line.split(" -> ").map{ it -> it.split(',').map { it.toInt() } }
            tmp.zipWithNext().forEach{
                (fst, snd) ->
                val (x0, y0) = fst
                val (x1, y1) = snd

                if (x0 != x1) {
                    for (x in (x0 toward x1)) {
                        res[Vector2(x, y1)] = Entity.ROCK
                    }
                } else if (y1 != y0) {
                    for (y in y0 toward y1) {
                        res[Vector2(x0, y)] = Entity.ROCK
                    }
                }
            }
        }

        return res
    }

    fun advance(field: MutableMap<Vector2, Entity>, coords: Vector2, floorLevel: Int = -1): Vector2? {
        val (x, y) = coords

        if (y == floorLevel) {
            field.remove(Vector2(x, y))
            field[Vector2(x, y-1)] = Entity.BALL
            return null
        }

        if (!field.containsKey(Vector2(x, y+1))) {
            field.remove(Vector2(x, y))
            field[Vector2(x, y+1)] = Entity.BALL
            return Vector2(x, y+1)
        }
        if (!field.containsKey(Vector2(x-1, y+1))) {
            field.remove(Vector2(x, y))
            field[Vector2(x-1, y+1)] = Entity.BALL
            return Vector2(x-1, y+1)
        }
        if  (!field.containsKey(Vector2(x+1, y+1))) {
            field.remove(Vector2(x, y))
            field[Vector2(x+1, y+1)] = Entity.BALL
            return Vector2(x+1, y+1)
        }

        return null
    }

    fun part1(input: List<String>): Int {
        val field = makeObstacles(input)

        val maxY = field.keys.maxBy { it.second }.second
        var curPos: Vector2? = source
        var balls = 0

        while (curPos!!.second <= maxY) {
            curPos = advance(field, curPos)
            if (curPos == null) {
                balls++
                curPos = source
            }
        }

        return balls
    }

    fun part2(input: List<String>): Int {
        val field = makeObstacles(input)

        val maxY = field.keys.maxBy { it.second }.second
        var curPos: Vector2? = source
        var balls = 0

        while (true) {
            curPos = advance(field, curPos!!, floorLevel=maxY+2)
            if (field[source] == Entity.BALL) {
                return ++balls
            }
            if (curPos == null) {
                balls++
                curPos = source
                field[source] = Entity.BALL
            }
        }

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 24)
    check(part2(testInput) == 93)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}
