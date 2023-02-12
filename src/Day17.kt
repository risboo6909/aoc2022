const val START_COL = 3
const val ROW_OFFSET = 4
const val GLASS_WIDTH = 7

const val GLASS_HEIGHT = 20000
const val TOTAL_ITERS = 1_000_000_000_000


data class Piece(var coords: Vector2, val pieceIdx: Int)

fun main() {

    val pieces = arrayOf(
        arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(1, 1, 1, 1),
        ),
        arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 1, 0, 0),
            intArrayOf(1, 1, 1, 0),
            intArrayOf(0, 1, 0, 0),
        ),
        arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 1, 0),
            intArrayOf(0, 0, 1, 0),
            intArrayOf(1, 1, 1, 0),
        ),
        arrayOf(
            intArrayOf(1, 0, 0, 0),
            intArrayOf(1, 0, 0, 0),
            intArrayOf(1, 0, 0, 0),
            intArrayOf(1, 0, 0, 0),
        ),
        arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(1, 1, 0, 0),
            intArrayOf(1, 1, 0, 0),
        ),
    )

    fun parse(input: List<String>): List<Direction?> {
        val res = emptyList<Direction?>().toMutableList()
        for (s in input[0]) {
            res.add(
                when (s) {
                    '<' -> Direction.LEFT
                    '>' -> Direction.RIGHT
                    else -> null
                }
            )
        }
        return res
    }

    fun isOverride(glass: Array<Array<Int>>, piece: Piece): Boolean {
        val (row, col) = piece.coords
        val data = pieces[piece.pieceIdx]

        for (y in data.indices) {
            for (x in 0 until data[y].size) {
                if (data[y][x] == 0) {
                    continue
                }
                if (glass[row-y][col+x] == 1) {
                    return true
                }
            }
        }
        return false
    }

    fun copyToGlass(glass: Array<Array<Int>>, piece: Piece) {
        val (row, col) = piece.coords
        val data = pieces[piece.pieceIdx]

        for (y in data.indices) {
            for (x in 0 until data[y].size) {
                if (data[y][x] != 0) {
                    glass[row - y][col + x] = data[y][x]
                }
            }
        }
    }

    fun findTopPoint(glass: Array<Array<Int>>, fromRow: Int): Int {
        for (row in fromRow until glass.size) {
            if (glass[row].filter{it==1}.size == 2) {
                return row - 1
            }
        }
        return glass.size
    }

    fun update(glass: Array<Array<Int>>, piece: Piece, dir: Direction): Pair<Boolean, Piece?> {
        var newCoords = piece.coords

        when (dir) {
            Direction.LEFT -> {
                newCoords = piece.coords.copy(second = piece.coords.second - 1)
            }
            Direction.RIGHT -> {
                newCoords = piece.coords.copy(second = piece.coords.second + 1)
            }
            else -> {}
        }

        if (isOverride(glass, Piece(newCoords, piece.pieceIdx))) {
            // unable to move a piece left or right, restore coords
            newCoords = piece.coords.copy()
        }

        newCoords = newCoords.copy(first = piece.coords.first - 1)
        if (isOverride(glass, Piece(newCoords, piece.pieceIdx))) {
            // unable to move a piece down
            copyToGlass(glass, Piece(newCoords.copy(first = newCoords.first + 1),
                                     piece.pieceIdx)
            )
            return Pair(false, null)
        }

        return Pair(true, Piece(newCoords, piece.pieceIdx))
    }

    fun makeBorders(glass: Array<Array<Int>>): Array<Array<Int>> {
        for (row in glass.indices) {
            glass[row][0] = 1
            glass[row][GLASS_WIDTH+1] = 1
        }
        for (col in 0 .. GLASS_WIDTH) {
            glass[0][col] = 1
        }
        return glass
    }

    fun printMe(glass: Array<Array<Int>>) {
        val top = findTopPoint(glass, 0)
        println("\n")
        for  (row in top downTo 0) {
            for (e in glass[row]) {
                if (e == 1) {
                    print("#")
                } else {
                    print(".")
                }
            }
            println()
        }
    }

    fun findLine(glass: Array<Array<Int>>, fromRow: Int, toRow: Int, needle: String): Pair<Int, Boolean> {
        for (row in fromRow + 1 .. toRow) {
            if (glass[row].asList().joinToString(separator = "") == needle) {
                return Pair(row, true)
            }
        }
        return Pair(fromRow, false)
    }

    fun dropSomePieces(input: List<String>, maxPieces: Int): Array<Array<Int>> {
        var pieceIdx = 0
        var dirIdx = 0
        var piecesTotal = 0
        var topPoint = 0

        // create big glass for pieces
        val glass = makeBorders(Array(GLASS_HEIGHT) {Array(GLASS_WIDTH+2) {0} })

        // create initial piece
        var piece: Piece? = Piece(Vector2(ROW_OFFSET + 3, START_COL), pieceIdx)

        // parse input
        val parsed = parse(input)

        while (piecesTotal < maxPieces) {
            val res = update(glass, piece!!, parsed[dirIdx%parsed.size]!!)
            val isMoved = res.first

            piece = res.second
            if (!isMoved) {
                pieceIdx = (pieceIdx + 1) % pieces.size
                topPoint = findTopPoint(glass, topPoint)
                piece = Piece(Vector2(topPoint + ROW_OFFSET + 3, START_COL), pieceIdx)
                piecesTotal++
            }
            dirIdx++
        }

        return glass
    }

    fun part1(input: List<String>): Int {
        val glass = dropSomePieces(input, 2022)
        return findTopPoint(glass, 0)
    }

    fun findSeparators(input: List<String>, maxPieces: Int): Set<String> {
        val glass = dropSomePieces(input, maxPieces)

        // find cycles
        val topPoint = findTopPoint(glass, 0)
        val cycleStarts = mutableSetOf<String>()

        var maxHeight = 0

        for (cyclePeriod in (1..2)) {
            var cyclePhase = 0

            for (j in (1..topPoint)) {
                var startRow = j
                val sep = glass[startRow]

                var height = 0
                var contentHash = 0
                var found = 0

                for (k in (startRow + 1..topPoint)) {
                    if (sep.contentEquals(glass[k])) {
                        if ((cyclePhase % cyclePeriod) == 0) {
                            val tmp = glass.sliceArray(startRow + 1 until k).flatten().hashCode()
                            if (contentHash == 0) {
                                height = k - startRow
                                if (height < maxHeight) {
                                    break
                                }
                                contentHash = tmp
                                startRow = k
                            } else if (contentHash == tmp) {
                                startRow = k
                                found++
                            }
                        }
                        if (found == 1) {
                            break
                        }
                        cyclePhase++
                    }
                }

                if (height > maxHeight && height > 1 && found == 1) {
                    maxHeight = height
                    cycleStarts.add(sep.toList().joinToString(separator = ""))
                }

            }
        }

        return cycleStarts
    }

    fun part2Helper(input: List<String>, sep: String, maxPieces: Int): Long? {
        var pieceIdx = 0
        var piecesTotal = 0
        var iteration = 0
        var prevSeparatorRowIdx = 0
        var savedPrevSeparatorRowIdx = 0
        var prevPatternFoundPiecesCount = 0
        var firstFullPiecesTotal = 0
        var firstPatternFoundRowIdx = 0
        var prefixHeight = 0

        var cycleHeight = 0
        var cycleHeightIter = 0
        var topPoint = 0

        val deltas = emptySet<Int>().toMutableSet()
        val heights = mutableMapOf<Int, Int>()

        // create big glass for pieces
        val glass = makeBorders(Array(GLASS_HEIGHT) {Array(GLASS_WIDTH+2) {0} })

        // create initial piece
        var piece: Piece? = Piece(Vector2(ROW_OFFSET + 3, START_COL), pieceIdx)

        // parse input
        val parsed = parse(input)

        while (piecesTotal < maxPieces) {

            val res = update(glass, piece!!, parsed[iteration % parsed.size]!!)
            val isMoved = res.first

            piece = res.second
            if (!isMoved) {
                pieceIdx = (pieceIdx + 1) % pieces.size

                topPoint = findTopPoint(glass, topPoint)
                piece = Piece(Vector2(topPoint + ROW_OFFSET + 3, START_COL), pieceIdx)
                piecesTotal++

                val (newSeparatorRowIdx, found) = findLine(glass, prevSeparatorRowIdx, topPoint, sep)

                if (found) {
                    val blockHeight = newSeparatorRowIdx - prevSeparatorRowIdx

                    if (prefixHeight == 0) {
                        prefixHeight = blockHeight
                        firstFullPiecesTotal = piecesTotal
                        firstPatternFoundRowIdx = newSeparatorRowIdx
                    }

                    if (deltas.contains(blockHeight)) {
                        deltas.clear()
                        if (prevSeparatorRowIdx - savedPrevSeparatorRowIdx == cycleHeight) {
                            // we've found a cycle, hurray, compute everything else
                            val piecesCycle = piecesTotal - prevPatternFoundPiecesCount
                            val delta = TOTAL_ITERS - firstFullPiecesTotal - 1
                            val k = (delta / piecesCycle) * cycleHeight + prefixHeight
                            val m = heights[(delta % piecesCycle).toInt()]!!

                            return k + m
                        }

                        cycleHeight = prevSeparatorRowIdx - savedPrevSeparatorRowIdx
                        savedPrevSeparatorRowIdx = prevSeparatorRowIdx
                        prevPatternFoundPiecesCount = piecesTotal
                    }
                    deltas.add(blockHeight)
                    prevSeparatorRowIdx = newSeparatorRowIdx
                } else if (prefixHeight != 0) {
                    heights[cycleHeightIter] = topPoint - firstPatternFoundRowIdx
                    cycleHeightIter++
                }
            }

            iteration++
        }

        return null

    }

    fun part2(input: List<String>, seps: Set<String>, maxPieces: Int): Long {
        return seps.
               parallelStream().
               map{part2Helper(input, it, maxPieces)}.
               filter{it!=null}.
               findFirst().
               get()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")

    var cyclesStarts = findSeparators(testInput, 400)
    check(part1(testInput) == 3068)
    check(part2(testInput, cyclesStarts, 2022) == 1514285714288)

    val input = readInput("Day17")

    println(part1(input))
    cyclesStarts = findSeparators(input, 5000)
    println(part2(input, cyclesStarts, 10000))
}
