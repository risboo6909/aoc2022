val normals = arrayOf (
    Vector3(1, 0, 0),
    Vector3(0, 1, 0),
    Vector3(0, 0, 1),
    Vector3(-1, 0, 0),
    Vector3(0, -1, 0),
    Vector3(0, 0, -1),
)

fun main() {

    fun parse(input: List<String>): List<Vector3> {
        val coords = input.map{ it ->
                val tmp = it.split(",").map{it.toInt()}
            Vector3(tmp[0], tmp[1], tmp[2])    // x, y, z
        }
        return coords
    }

    fun getNeighbours(pos: Vector3, topLeft: Vector3, bottomRight: Vector3): MutableSet<Vector3> {
        val res = mutableSetOf<Vector3>()

        // left
        if (pos.first - 1 >= topLeft.first) {
            res.add(Vector3(pos.first - 1, pos.second, pos.third))
        }
        // right
        if (pos.first + 1 <= bottomRight.first) {
            res.add(Vector3(pos.first + 1, pos.second, pos.third))
        }
        // top
        if (pos.second - 1 >= topLeft.second) {
            res.add(Vector3(pos.first, pos.second - 1, pos.third))
        }
        // bottom
        if (pos.second + 1 <= bottomRight.second) {
            res.add(Vector3(pos.first, pos.second + 1, pos.third))
        }
        // z-
        if (pos.third - 1 >= topLeft.third) {
            res.add(Vector3(pos.first, pos.second, pos.third - 1))
        }
        // z+
        if (pos.third + 1 <= bottomRight.third) {
            res.add(Vector3(pos.first, pos.second, pos.third + 1))
        }

        return res
    }

    fun countFaces(cubes: Set<Vector3>, cube: Vector3): Int {
        return getNeighbours(cube,
            Vector3(-100, -100, -100),
            Vector3(100, 100, 100)).
        sumOf {
            1 - cubes.contains(it).toInt()
        }
    }

    fun part1(input: List<String>): Int {
        val cubes = parse(input).toSet()
        return cubes.sumOf { countFaces(cubes, it) }
    }

    fun allCubesInside(cubes: Set<Vector3>, topLeft: Vector3, bottomRight: Vector3): Boolean {
        fun isInside(cube: Vector3): Boolean {
            return (topLeft.first < cube.first) && (cube.first < bottomRight.first) &&
                   (topLeft.second < cube.second) && (cube.second < bottomRight.second) &&
                   (topLeft.third < cube.third) && (cube.third < bottomRight.third)
        }
        return cubes.all { isInside(it) }
    }

    fun part2(input: List<String>): Int {
        val cubes = parse(input).toSet()

        val cubesSorted = cubes.sortedWith(
            compareBy { it.first * it.first + it.second * it.second + it.third * it.third }
        )

        var topLeft = cubesSorted.first()
        var bottomRight = cubesSorted.last()

        while (!allCubesInside(cubes, topLeft, bottomRight)) {
            topLeft = topLeft.sub(Vector3(1, 1, 1))
            bottomRight = bottomRight.add(Vector3(1, 1, 1))
        }

        var toGo = getNeighbours(topLeft, topLeft, bottomRight)
        val checked = mutableSetOf<Vector3>()

        while (toGo.isNotEmpty()) {
            val currentPos = toGo.last()
            toGo.remove(currentPos)

            toGo = toGo.union(
                getNeighbours(currentPos, topLeft, bottomRight)
            ).filter { !checked.contains(it) && !cubes.contains(it) }.toMutableSet()

            checked.add(currentPos)
        }

        return cubes.sumOf {
            cube -> normals.sumOf {
                checked.contains(cube.add(it)).toInt()
            }
        }

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 64)
    check(part2(testInput) == 58)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}
