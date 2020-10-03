package com.shinonometn.media.cue

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.*

typealias CueSheetReadWarning = Pair<CueSheetLine, String>

/*
*
* Cue sheet file
*
* */
class CueSheetFile {

    var originFile: File? = null
    var fileCharset: Charset = Charset.forName("UTF8")

    val metaInfo: MutableList<Pair<CueMetaFieldType, CueSheetLine>> = LinkedList()
    val trackList: MutableList<CueSheetTrack> = LinkedList()

    var errorInfo = LinkedList<CueSheetReadWarning>()
        private set

    val lines: MutableList<CueSheetLine> = LinkedList()

    /*
    *
    *
    * */

    fun loadFile(file: File, charset: Charset = Charset.forName("UTF8")) {

        originFile = file
        fileCharset = charset

        metaInfo.clear()
        trackList.clear()
        errorInfo.clear()

        lines.addAll(BufferedReader(InputStreamReader(FileInputStream(file), charset)).use { reader ->
            reader.lineSequence().mapIndexed { index, s ->
                CueSheetLine(number = index + 1, line = s)
            }.toMutableList()
        })

        CueSheetPaser(this).parse()
    }
}
