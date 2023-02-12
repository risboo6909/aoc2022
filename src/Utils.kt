import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

typealias Vector2 = Pair<Int, Int>  // x, y
typealias Vector3 = Triple<Int, Int, Int>   // x, y, z

fun Vector3.add(v: Vector3): Vector3 = Vector3(this.first + v.first, this.second + v.second, this.third + v.third)

fun Vector3.sub(v: Vector3): Vector3 = this.add(Vector3(-v.first, -v.second, -v.third))

fun Boolean.toInt() = if (this) 1 else 0

enum class Direction {
    LEFT,
    RIGHT,
    UP,
    DOWN,
}

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')
