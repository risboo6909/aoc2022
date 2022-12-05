const val Empty = "    "

data class Instruction(val count: Int, val from: Int, val to: Int)

typealias BucketsMap = MutableMap<Int, MutableList<Char>>

fun main() {

    fun parseBuckets(input: List<String>): BucketsMap {
        val stacks: BucketsMap = mutableMapOf()

        outer@for (line in input) {
            for ((i, chunk) in line.chunked(4).withIndex()) {
                if (chunk == Empty) {
                    continue
                }

                val (_, s, _, _) = chunk.toCharArray()
                if (s.isDigit()) {
                    break@outer
                }

                if (!stacks.containsKey(i+1)) {
                    stacks[i+1] = mutableListOf()
                }
                stacks[i+1]!!.add(s)

            }
        }

        for ((_, v) in stacks) {
            v.reverse()
        }

        return stacks

    }

    fun parseInstructions(input: List<String>): List<Instruction> {
        val instructions = mutableListOf<Instruction>()

        for (line in input) {
            if (!line.startsWith("move")) {
                continue
            }

            val components = line.split(' ')
            val instr = Instruction(
                count = components[1].toInt(),
                from = components[3].toInt(),
                to = components[5].toInt(),
            )

            instructions.add(instr)
        }

        return instructions
    }

    fun getTopItems(buckets: BucketsMap): String {
        val res = mutableListOf<Char>()
        for (k in buckets.keys.sorted()) {
            res.add(buckets[k]!!.last())
        }

        return res.joinToString("")
    }

    fun part1(input: List<String>): String {
        val buckets = parseBuckets(input)
        val instructions = parseInstructions(input)

        for (instr in instructions) {
            for (i in 0 until instr.count) {
                val e = buckets[instr.from]!!.removeLast()
                buckets[instr.to]!!.add(e)
            }
        }

        return getTopItems(buckets)
    }

    fun part2(input: List<String>): String {
        val buckets = parseBuckets(input)
        val instructions = parseInstructions(input)

        for (instr in instructions) {
            val lastN = buckets[instr.from]!!.takeLast(instr.count)
            buckets[instr.from] = (buckets[instr.from]!!.dropLast(instr.count)).toMutableList()
            buckets[instr.to] = (buckets[instr.to]!! + lastN).toMutableList()
        }

        return getTopItems(buckets)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
