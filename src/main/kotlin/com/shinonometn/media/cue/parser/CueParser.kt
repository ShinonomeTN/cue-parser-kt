package com.shinonometn.media.cue.parser

import com.shinonometn.media.cue.core.CueTreeNodeImpl
import com.shinonometn.media.cue.core.CueTreeNodeType
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

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
@Suppress("FunctionName")
fun CueParser(input: InputStream): CueTreeNodeImpl {
    val root = CueTreeNodeImpl(CueTreeNodeType.ROOT)
    var visitor = root

    var lineNumber = 0

    BufferedReader(InputStreamReader(input)).use { reader ->
        reader.lineSequence().forEach { line ->
            lineNumber++
            val segments = parseLineSegments(line).takeIf { it.isNotEmpty() } ?: return@use
            val command = segments[0]

            val currentNodeType = CueTreeNodeType.literalCommandMappings[command] ?: CueTreeNodeType.META
            val handler = currentNodeType.directiveHandler
            visitor = handler(currentNodeType, visitor, segments, lineNumber)
        }
    }

    return root
}

/**
 * Create a cue iterator, allowing to read cue content line by line.
 * Read cue content from an Iterable, it will assume lines are in order.
 * If the line that been processed wasn't created any node, next() will just return the last node.
 */
class CueParser(lines: Iterable<String>, root: CueTreeNodeImpl = CueTreeNodeImpl(CueTreeNodeType.ROOT)) : Iterator<CueTreeNodeImpl> {
    var current = root
        private set

    var currentLineNumber = 0

    val iterator = lines.iterator()

    override fun next(): CueTreeNodeImpl {
        val line = iterator.next()
        currentLineNumber++

        val segments = parseLineSegments(line).takeIf { it.isNotEmpty() } ?: return current
        val command = segments[0]

        val currentNodeType = CueTreeNodeType.literalCommandMappings[command] ?: CueTreeNodeType.META
        val handler = currentNodeType.directiveHandler
        current = handler(currentNodeType, current, segments, currentLineNumber)

        return current
    }

    override fun hasNext(): Boolean {
        return iterator.hasNext()
    }
}