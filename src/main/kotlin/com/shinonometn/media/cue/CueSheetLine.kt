package com.shinonometn.media.cue

import java.util.*
import kotlin.collections.ArrayList

/*
*
* Cue line entity
*
* */
data class CueSheetLine(val number: Int = 0, var line: String) {

    /**
     * Sections parsed
     * */
    val sections: MutableList<String> = parseLine(line.trim())

    /**
     * Convinience property to check if the line is comment;
     * */
    val isComment: Boolean = line.isBlank() || sections[0][0] == ';'

    /**
     * Line command
     * */
    val command: String = if (isComment) {
        if (line.isEmpty()) "" else ";"
    } else sections[0]

    //
// Function that parsing each line
//
    private fun parseLine(line: String): MutableList<String> {
        if (line.isEmpty()) return ArrayList()

        var quoting = false
        val sb = StringBuilder()
        val result = LinkedList<String>()

        for (char: Char in line) {
            if (char == ' ' && !quoting) {
                result.add(sb.toString())
                sb.clear()
                continue
            }

            if (char == '"' || char == '\'') {
                quoting = !quoting
                continue
            }

            sb.append(char)
        }

        if (sb.isNotEmpty()) result.add(sb.toString())
        return result
    }
}