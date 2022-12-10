enum class OpType(val ticks: Int) {
    ADDX(2),
    NOOP(1),
}

class Computer {
    private val ops: MutableList<Pair<OpType, Int>> = mutableListOf()

    private var ip = 0
    private var tick = 0
    private var nextInc = 0

    var reg = 1

    private fun doOp(op: OpType, arg: Int) {
        when (op) {
            OpType.ADDX -> reg += arg
            else -> Unit
        }
    }

    private fun getTicks(op: OpType): Int {
        return op.ticks
    }

    fun loadProgram(program: List<String>) {
        for (line in program) {
            if (line.startsWith("noop")) {
                ops.add(Pair(OpType.NOOP, 1))
            } else if (line.startsWith("addx")) {
                val (_, ticks) = line.split(" ")
                ops.add(Pair(OpType.ADDX, ticks.toInt()))
            }
        }
        nextInc = getTicks(ops[ip].first)
    }

    fun advance(): Int {
        if (tick == nextInc) {
            doOp(ops[ip].first, ops[ip].second)
            if (++ip == ops.size) {
                return -1
            }

            nextInc += getTicks(ops[ip].first)
        }

        return ++tick
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        var res = 0
        val checkpoints = setOf(20, 60, 100, 140, 180, 220)

        val comp = Computer()
        comp.loadProgram(input)

        while (true) {
            val tick = comp.advance()
            if (tick < 0) {
                break
            }

            if (tick in checkpoints) {
                res += tick * comp.reg
            }
        }

        return res
    }

    fun part2(input: List<String>): String {
        var crtPos = 0
        val array = Array(6) { IntArray(40) }

        val comp = Computer()
        comp.loadProgram(input)

        while (true) {
            val tick = comp.advance()
            if (tick < 0) {
                break
            }
            val crtPosX = crtPos % 40
            if ((crtPosX == comp.reg-1) || (crtPosX == comp.reg) || (crtPosX == comp.reg+1)) {
                array[crtPos / 40][crtPosX] = 1
            }
            crtPos++
        }

        return array.joinToString("\n") { it ->
            it.joinToString("") {
                if (it == 0) "." else "#"
            }
        }

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13140)
    check(part2(testInput) ==
    "##..##..##..##..##..##..##..##..##..##..\n" +
    "###...###...###...###...###...###...###.\n" +
    "####....####....####....####....####....\n" +
    "#####.....#####.....#####.....#####.....\n" +
    "######......######......######......####\n" +
    "#######.......#######.......#######....."
    )

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
