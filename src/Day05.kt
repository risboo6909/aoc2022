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

        input.forEach {
            if (it.startsWith("move")) {
                val components = it.split(' ')
                val instr = Instruction(
                    count = components[1].toInt(),
                    from = components[3].toInt(),
                    to = components[5].toInt(),
                )
                instructions.add(instr)
            }
        }

        return instructions
    }

    fun getTopItems(buckets: BucketsMap): String {
        return buckets.keys
            .sorted()
            .map{buckets[it]!!.last()}
            .joinToString("")
    }

    fun part1(input: List<String>): String {
        val buckets = parseBuckets(input)
        val instructions = parseInstructions(input)

        instructions.forEach {
            (0 until it.count).forEach {
                    _ -> buckets[it.to]!!.add(buckets[it.from]!!.removeLast())
            }
        }

        return getTopItems(buckets)
    }

    fun part2(input: List<String>): String {
        val buckets = parseBuckets(input)
        val instructions = parseInstructions(input)

        instructions.forEach {
            val lastN = buckets[it.from]!!.takeLast(it.count)
            buckets[it.from] = buckets[it.from]!!.dropLast(it.count).toMutableList()
            buckets[it.to]!! += lastN
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
