package com.shinonometn.media.cue.parser

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

//
// Property keys
//

const val CUE_PROPERTY_FILE_NAME = "filename"
const val CUE_PROPERTY_FILE_TYPE = "file_type"

const val CUE_PROPERTY_TRACK_NUMBER = "track_number"
const val CUE_PROPERTY_TRACK_TYPE = "track_type"

const val CUE_PROPERTY_TRACK_INDEX_PREFIX = "index@"

const val CUE_PROPERTY_COMMENT_PREFIX = "comment@"
const val CUE_PROPERTY_REM_COMMENT_PREFIX = "remark_comment@"

//
// Cue Directive argument
//
internal typealias CueArgument = List<String>

internal fun CueArgument.getDirective() = if (isEmpty()) "" else this[0]
internal fun CueArgument.getParameters(): List<String> = if (size < 2) emptyList() else subList(1, size)

private fun parseLineSegments(s: String): List<String> {
    val line = s.trim()
    if (line.isEmpty()) return emptyList()
    if (line[0] == ';' || line[0] == '\uFEFF') return listOf(";", line.substring(line.indexOfFirst { it == ';' } + 1))

    val segments = LinkedList<String>()
    val buffer = StringBuilder()

    var quoting = false
    for (char in line) when (char) {

        ' ' -> if (!quoting && buffer.isNotEmpty()) {
            segments.add(buffer.toString()); buffer.clear()
        } else {
            buffer.append(char)
        }

        '"' -> quoting = !quoting

        else -> buffer.append(char)
    }

    if (buffer.isNotEmpty()) segments.add(buffer.toString())

    return segments
}

/**
 * Read cue content from a InputStream
 * Return a CueNode as information tree root
 */
fun CueParser(input: InputStream): CueNode {
    val root =
        CueNode(CueNodeType.ROOT)
    var visitor = root

    var lineNumber = 0

    BufferedReader(InputStreamReader(input)).use { reader ->
        reader.lineSequence().forEach { line ->
            lineNumber++
            val segments = parseLineSegments(line).takeIf { it.size >= 0 } ?: return@use
            val command = segments[0]

            val currentNodeType = commandMap[command] ?: CueNodeType.META
            val handler = currentNodeType.directiveHandler
            visitor = handler(currentNodeType, visitor, segments, lineNumber)
        }
    }

    return root
}

/**
 * Create a cue iterator, allowing to read cue content line by line.
 * Read cue content from a Iterable, it will assume lines are in order.
 * If the line that been processed wasn't created any node, next() will just returning the last node.
 */
class CueContentParser(
    lines: Iterable<String>, val root: CueNode = CueNode(
        CueNodeType.ROOT
    )
) : Iterator<CueNode> {
    var current = root
        private set

    var currentLineNumber = 0

    val iterator = lines.iterator()

    override fun next(): CueNode {
        val line = iterator.next()
        currentLineNumber++

        val segments = parseLineSegments(line).takeIf { it.size >= 0 } ?: return current
        val command = segments[0]

        val currentNodeType = commandMap[command] ?: CueNodeType.META
        val handler = currentNodeType.directiveHandler
        current = handler(currentNodeType, current, segments, currentLineNumber)

        return current
    }

    override fun hasNext(): Boolean {
        return iterator.hasNext()
    }
}

//fun main() {
//    println("Read CUE in one time.")
//    val cue = ReadCueFileAsTree(FileInputStream(File("sample.cue")))
//    println(cue)
//    println()
//    println("Read CUE line by line")
//    val iterator =
//        CueContentParser(BufferedReader(FileReader(File("sample.cue"))).readLines())
//    while(iterator.hasNext()) {
//        iterator.next()
//        println("Line ${iterator.currentLineNumber}")
//    }
//    println(iterator.root)
//}