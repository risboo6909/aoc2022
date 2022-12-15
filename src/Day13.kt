sealed class Item {
    class Value(val value: Int): Item()
    class Nested(val value: List<Item>): Item()
}

fun main() {
    fun parseNumber(line: String, startIdx: Int): Pair<Int, Int> {
        val tmp = line.substring(startIdx).takeWhile { it.isDigit() }
        return Pair(tmp.toInt(), tmp.length)
    }

    fun parseOne(line: String): List<Item> {
        fun inner(startIdx: Int): Pair<List<Item>, Int> {
            var idx = startIdx
            val res: MutableList<Item> = mutableListOf()

            while (idx < line.length) {
                val s = line[idx]
                if (s.isDigit()) {
                    val (n, toSkip) = parseNumber(line, idx)
                    res.add(Item.Value(n))
                    idx += toSkip
                    continue
                } else if (s == '[') {
                    val (tmp, newIdx) = inner(idx+1)
                    idx = newIdx
                    res.add(Item.Nested(tmp))
                } else if (s == ']') {
                    return Pair(res, idx)
                }

                idx++
            }

            return Pair(res, idx)
        }

        val (parsed, _) = inner(0)
        return (parsed[0] as Item.Nested).value
    }

    fun compare(first: List<Item>, second: List<Item>): Int {
        fun inner(a: List<Item>, b: List<Item>): Int {
            for (idx in 0 until kotlin.math.min(a.size, b.size)) {
                if (a[idx]::class == b[idx]::class) {
                    when(a[idx]) {
                        is Item.Nested -> {
                            val res = inner((a[idx] as Item.Nested).value, (b[idx] as Item.Nested).value)
                            if (res != 0) return res
                        }
                        is Item.Value -> {
                            if ((a[idx] as Item.Value).value > (b[idx] as Item.Value).value) {
                                return 1
                            } else if ((a[idx] as Item.Value).value < (b[idx] as Item.Value).value) {
                                return -1
                            }
                        }
                    }
                } else {
                    when(a[idx]) {
                        is Item.Nested -> {
                            val res = inner((a[idx] as Item.Nested).value, listOf(b[idx]))
                            if (res != 0) return res
                        }
                        is Item.Value -> {
                            val res = inner(listOf(a[idx]), (b[idx] as Item.Nested).value)
                            if (res != 0) return res
                        }
                    }
                }
            }
            if (b.size > a.size) {
                return -1
            } else if (b.size == a.size) {
                return 0
            }
            return 1
        }

        return inner(first, second)
    }

    fun parse(input: List<String>): List<Pair<List<Item>, List<Item>>> {
        val iter = input.iterator()
        val res: MutableList<Pair<List<Item>, List<Item>>> = mutableListOf()

        while (iter.hasNext()) {
            val first = parseOne(iter.next())
            val second = parseOne(iter.next())
            res.add(Pair(first, second))
            if (iter.hasNext()) {
                iter.next()
            }
        }
        return  res
    }

    fun part1(input: List<String>): Int {
        return parse(input).withIndex().sumOf { (i, pair) -> if (compare(pair.first, pair.second) == -1) i + 1 else 0 }
    }

    fun part2(input: List<String>): Int {
        val dividers = parse(listOf("[[2]]", "[[6]]"))
        val flatParsed = (parse(input) + dividers).flatMap { listOf(it.first, it.second) }.toMutableSet()

        val res = flatParsed.sortedWith{o1, o2 -> compare(o1, o2)}
        return ((res.indexOf(dividers.first().first)+1) *
                (res.indexOf(dividers.first().second)+1))
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}
