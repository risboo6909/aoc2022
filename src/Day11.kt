var M = 1.toULong()

data class Monkey(var items: ArrayDeque<ULong>, var op: ((ULong) -> ULong)?, var test: ((ULong) -> Int)?, var inspect: ULong,
                  private val useModulo: Boolean) {
    constructor(useModulo: Boolean) : this(ArrayDeque<ULong>(), null, null, 0.toULong(), useModulo)
}

fun main() {

    fun parse(input: List<String>, useModulo: Boolean): List<Monkey> {
        val res: MutableList<Monkey> = mutableListOf()
        val it = input.listIterator()

        while (it.hasNext()) {

            val line = it.next()

            if (line.startsWith("Monkey")) {
                res.add(Monkey(useModulo))
                continue
            }

            if (line.startsWith("  Starting items:")) {
                val (_, itemsAsStr) = line.split(": ")
                res.last().items.addAll(itemsAsStr.split(", ").map{it.toULong()})
            } else if (line.startsWith("  Operation:")) {
                val (_, whatToDo) = line.split(": ")
                if (whatToDo.split("*").size == 2) {
                    val (_, n) = whatToDo.split(" * ")
                    if (useModulo) {
                        if (n == "old") {
                            res.last().op = { x -> (x % M * x % M) % M }
                        } else {
                            res.last().op = { x -> (x % M * n.toULong()) % M }
                        }
                    } else {
                        if (n == "old") {
                            res.last().op = { x -> (x * x) }
                        } else {
                            res.last().op = { x -> (x * n.toULong()) }
                        }
                    }
                } else if (whatToDo.split("+").size == 2) {
                    val (_, n) = whatToDo.split(" + ")
                    if (useModulo) {
                        if (n == "old") {
                            res.last().op = { x -> (x % M + x % M) % M }
                        } else {
                            res.last().op = { x -> (x % M + n.toULong()) % M }
                        }
                    } else {
                        if (n == "old") {
                            res.last().op = { x -> (x + x) }
                        } else {
                            res.last().op = { x -> (x + n.toULong()) }
                        }
                    }
                }
            } else if (line.startsWith("  Test:")) {
                val (_, whatToDo) = line.split(": ")
                val (_, _, n) = whatToDo.split(" ")

                M *= n.toULong()
                val posCond = it.next().filter{ it.isDigit() }.toInt()
                val negCond = it.next().filter{ it.isDigit() }.toInt()
                res.last().test = {x -> if (x % n.toULong() == 0.toULong()) posCond else negCond}
            }
        }

        return res
    }

    fun compute(input: List<String>, iterations: Int, divideBy: ULong, useModulo: Boolean): ULong {
        M = 1.toULong()

        val monkeys = parse(input, useModulo)
        for (iter in 0 until iterations) {
            for (monkey in monkeys) {
                while (monkey.items.size > 0) {
                    val new = (monkey.op!!(monkey.items.removeFirst()) / divideBy)
                    val toMonkey = monkey.test!!(new)

                    monkey.inspect++
                    monkeys[toMonkey].items.addLast(new)
                }
            }
        }
        return monkeys.sortedByDescending { it.inspect }.slice((0..1)).map{it.inspect}.reduce { a, b -> a * b }
    }

    fun part1(input: List<String>): ULong {
        return compute(input, 20, 3.toULong(), false)
    }

    fun part2(input: List<String>): ULong {
        return compute(input, 10000, 1.toULong(), true)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 10605.toULong())
    check(part2(testInput) == 2713310158.toULong())

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}
