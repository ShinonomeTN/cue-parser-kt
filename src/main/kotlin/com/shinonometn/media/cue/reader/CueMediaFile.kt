package com.shinonometn.media.cue.reader

import com.shinonometn.media.cue.core.CueTreeNode
import com.shinonometn.media.cue.core.CueTreeNodeType

/**
 * Return the first file that defined in CUE sheet
 */
fun CueInfoReader.mediaFile(): CueMediaFile {
    return rootNode.children.filter { it.type == CueTreeNodeType.FILE }.map(::CueMediaFile).first()
}

/**
 * Return all files that defined in CUE sheet
 */
fun CueInfoReader.mediaFileList(): List<CueMediaFile> {
    return rootNode.children.filter { it.type == CueTreeNodeType.FILE }.map(::CueMediaFile)
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
class CueMediaFile internal constructor(val node: CueTreeNode) {
    val filename: String = node.properties["0"] ?: ""
    val type: String = node.properties["1"] ?: ""
}