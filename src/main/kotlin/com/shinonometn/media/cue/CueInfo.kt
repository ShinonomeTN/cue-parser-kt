package com.shinonometn.media.cue

import com.shinonometn.media.cue.parser.CueNode
import com.shinonometn.media.cue.parser.CueParser
import com.shinonometn.media.cue.reader.*
import java.io.File
import java.io.FileInputStream

/**
 * Cue info reader entrance.
 * After a parser read the CUE content, all information were contained in the result tree.
 * This is the default information extractor implementation.
 *
 * Detailed CUE file introductions see https://github.com/libyal/libodraw/blob/master/documentation/CUE%20sheet%20format.asciidoc
 */
class CueInfo(cueInfoRoot : CueNode) {
    val node = cueInfoRoot
}

//fun main() {
//    val cue = CueParser(FileInputStream(File("sample.cue")))
//    println(cue)
//    val cueInfo = CueInfo(cue)
//    println("Album info: ")
//    for(key in AlbumInfo.values()) {
//        val info = cueInfo.getInfo(key) ?: continue
//        println("|-${key.name.toLowerCase().replace("_", " ")}: $info")
//    }
//    println()
//    println("File & Track info Info:")
//    cueInfo.getMediaFileList().forEach { file ->
//        println("|-[${file.type}] File: ${file.filename}")
//        file.getTrackList().forEach {
//            println("  |-Track ${it.number} [${it.type}]")
//            for(key in TrackInfo.values()) {
//                val info = it.getInfo(key) ?: continue
//                println("    |-${key.name.toLowerCase().replace("_", " ")}: $info")
//            }
//        }
//    }
//}