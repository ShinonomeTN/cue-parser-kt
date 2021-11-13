package com.shinonometn.media.cue.reader

import com.shinonometn.media.cue.parser.CueTreeNode
import com.shinonometn.media.cue.parser.CueTreeNodeType

/**
 * Cue info reader entrance.
 * After a parser read the CUE content, all information were contained in the result tree.
 * This is the default information extractor implementation.
 *
 * Detailed CUE file introductions see https://github.com/libyal/libodraw/blob/master/documentation/CUE%20sheet%20format.asciidoc
 */
class CueInfoReader(cueInfoRoot: CueTreeNode) {
    init { require(cueInfoRoot.type == CueTreeNodeType.ROOT) { "Given tree node must be a ROOT node" } }
    val rootNode = cueInfoRoot
}

fun CueTreeNode.reader() : CueInfoReader {
    return CueInfoReader(this)
}