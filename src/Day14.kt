enum class Entity {
    ROCK,
    BALL,
}

private infix fun Int.toward(to: Int): IntProgression {
    val step = if (this > to) -1 else 1
    return IntProgression.fromClosedRange(this, to, step)
}

fun main() {
    val source = Coords(500, 0)

    fun makeObstacles(input: List<String>): MutableMap<Coords, Entity> {
        val res = mutableMapOf<Coords, Entity>()
        for (line in input) {
            val tmp = line.split(" -> ").map{ it -> it.split(',').map { it.toInt() } }
            tmp.zipWithNext().forEach{
                (fst, snd) ->
                val (x0, y0) = fst
                val (x1, y1) = snd

                if (x0 != x1) {
                    for (x in (x0 toward x1)) {
                        res[Coords(x, y1)] = Entity.ROCK
                    }
                } else if (y1 != y0) {
                    for (y in y0 toward y1) {
                        res[Coords(x0, y)] = Entity.ROCK
                    }
                }
            }
        }

        return res
    }

    fun advance(field: MutableMap<Coords, Entity>, coords: Coords, floorLevel: Int = -1): Coords? {
        val (x, y) = coords

        if (y == floorLevel) {
            field.remove(Coords(x, y))
            field[Coords(x, y-1)] = Entity.BALL
            return null
        }

        if (!field.containsKey(Coords(x, y+1))) {
            field.remove(Coords(x, y))
            field[Coords(x, y+1)] = Entity.BALL
            return Coords(x, y+1)
        }
        if (!field.containsKey(Coords(x-1, y+1))) {
            field.remove(Coords(x, y))
            field[Coords(x-1, y+1)] = Entity.BALL
            return Coords(x-1, y+1)
        }
        if  (!field.containsKey(Coords(x+1, y+1))) {
            field.remove(Coords(x, y))
            field[Coords(x+1, y+1)] = Entity.BALL
            return Coords(x+1, y+1)
        }

        return null
    }

    fun part1(input: List<String>): Int {
        val field = makeObstacles(input)

        val maxY = field.keys.maxBy { it.second }.second
        var curPos: Coords? = source
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
        var curPos: Coords? = source
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
