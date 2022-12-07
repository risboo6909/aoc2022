import java.lang.Exception

const val TOTAL_SPACE = 70_000_000
const val SPACE_NEEDED = 30_000_000

enum class NodeType {
    DIR,
    FILE,
}

class Node(val type: NodeType, val name: String, var size: Int, val parent: Node?, val children: MutableSet<Node>?) {
    fun attachNode(node: Node): Boolean {
        if (this.type == NodeType.FILE) {
            throw Exception("can't add children to file: ${this.name}")
        }

        // check if node doesn't exists
        this.children?.forEach {
            if (it.name == node.name) {
                return false
            }
        }

        this.children!!.add(node)
        return true
    }
}


fun main() {

    fun findDir(curDir: Node, dirName: String): Node {
        curDir.children?.filter{it.type == NodeType.DIR && it.name == dirName}?.forEach {
            return it
        }
        return curDir
    }

    fun updateSizes(_curDir: Node?, size: Int) {
        // update sizes for parents
        var curDir = _curDir
        while (curDir != null) {
            curDir.size += size
            curDir = curDir.parent
        }
    }

    fun buildHier(input: List<String>): Node {
        val root = Node(NodeType.DIR, "/", 0, null, mutableSetOf())
        var curDir = root

        for (line in input) {
            if (line.startsWith("$ cd")) {
                val (_, _, dirName) = line.split(' ')
                curDir = if (dirName == "..") {
                    curDir.parent
                } else {
                    findDir(curDir, dirName)
                }!!
                continue
            }
            if (!line.startsWith("$")) {
                val (something, name) = line.split(" ")
                if (something == "dir") {
                    curDir.attachNode(Node(NodeType.DIR, name, 0, curDir, mutableSetOf()))
                    continue
                }
                val size = something.toInt()
                if (curDir.attachNode(Node(NodeType.FILE, name, size, curDir, null))) {
                    updateSizes(curDir, size)
                }
            }
        }

        return root

    }

    fun part1(input: List<String>): Int {
        fun inner(curNode: Node): Int {
            val acc = curNode.children!!.filter{it.type == NodeType.DIR}.sumOf { inner(it) }
            if (curNode.size < 100_000) {
                return acc + curNode.size
            }
            return acc
        }

        return inner(buildHier(input))
    }

    fun part2(input: List<String>): Int {
        val root = buildHier(input)

        val spaceLeft = TOTAL_SPACE - root.size
        val spaceReq = SPACE_NEEDED - spaceLeft

        fun inner(curNode: Node): Int {
            var minFound = Int.MAX_VALUE
            curNode.children!!.filter{it.type == NodeType.DIR}.forEach {
                minFound = minOf(inner(it), minFound)
                if (it.size in spaceReq until minFound) {
                    minFound = it.size
                }
            }
            return minFound
        }

        return inner(root)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24933642)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))

}
