package com.shinonometn.media.cue.reader

import com.shinonometn.media.cue.CueInfo
import com.shinonometn.media.cue.parser.CUE_PROPERTY_FILE_NAME
import com.shinonometn.media.cue.parser.CUE_PROPERTY_FILE_TYPE
import com.shinonometn.media.cue.parser.CueNode
import com.shinonometn.media.cue.parser.CueNodeType

fun CueInfo.getMediaFileList(): List<CueMediaFile> {
    var visitor = node

    return visitor.children.filter {
        it.type == CueNodeType.FILE
    }.map {
        CueMediaFile(
            it.properties[CUE_PROPERTY_FILE_NAME] ?: "",
            it.properties[CUE_PROPERTY_FILE_TYPE] ?: "",
            it
        )
    }
}

/**
 * About CUE FILE command:
 *
 * The FILE command is used to specify a file that contains data.
 *
 * FILE [ filename ] [file type]
 *
 * If the filename contains any spaces, then it must be enclosed in quotation marks.
 * The first FILE command should be the commands in the CUE sheet with the exception of the CATALOG command.
 * */
class CueMediaFile internal constructor(
    val filename: String,
    val type: String,
    val node: CueNode
)