typealias MyPair = Pair<Boolean, Int>

class Field(val f: (MyPair, MyPair, MyPair, MyPair) -> Int): Iterable<Int> {
    val field: MutableList<List<Int>> = mutableListOf()

    override fun iterator(): Iterator<Int> {
        return object : Iterator<Int> {
            var col = -1
            var row = 0
            override fun hasNext(): Boolean = col*row < (field.size-1)*(field[0].size-1)
            override fun next(): Int {
                if (col == field[0].size-1) {
                    row++
                    col = 0
                } else {
                    col++
                }
                return f(isVisibleDown(row, col),
                         isVisibleUp(row, col),
                         isVisibleLeft(row, col),
                         isVisibleRight(row, col)
                )
            }
        }
    }

    fun addRow(row: List<Int>) {
        this.field.add(row)
    }

    fun isVisibleRight(row: Int, col: Int): MyPair {
        var dist = 0
        for (j in col+1 until this.field[row].size) {
            dist++
            if (this.field[row][j] >= this.field[row][col]) {
                return Pair(false, dist)
            }
        }
        return Pair(true, dist)
    }

    fun isVisibleLeft(row: Int, col: Int): MyPair {
        var dist = 0
        for (j in col-1 downTo 0) {
            dist++
            if (this.field[row][j] >= this.field[row][col]) {
                return Pair(false, dist)
            }
        }
        return Pair(true, dist)
    }

    fun isVisibleUp(row: Int, col: Int): MyPair {
        var dist = 0
        for (j in row-1 downTo 0) {
            dist++
            if (this.field[j][col] >= this.field[row][col]) {
                return Pair(false, dist)
            }
        }
        return Pair(true, dist)
    }

    fun isVisibleDown(row: Int, col: Int): MyPair {
        var dist = 0
        for (j in row+1 until this.field.size) {
            dist++
            if (this.field[j][col] >= this.field[row][col]) {
                return Pair(false, dist)
            }
        }
        return Pair(true, dist)
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val field = Field { down: MyPair, up: MyPair, left: MyPair, right: MyPair ->
            (down.first || up.first || left.first || right.first).compareTo(false)
        }
        for (line in input) {
            field.addRow(line.map{it.toString().toInt()})
        }
        return field.sum()
    }

    fun part2(input: List<String>): Int {
        val field = Field { down: MyPair, up: MyPair, left: MyPair, right: MyPair ->
            down.second * up.second * left.second * right.second
        }
        for (line in input) {
            field.addRow(line.map{it.toString().toInt()})
        }
        return field.max()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
