package com.shinonometn.media.cue.reader

import com.shinonometn.media.cue.parser.CueNode

/**
 * Cue info reader entrance.
 * After a parser read the CUE content, all information were contained in the result tree.
 * This is the default information extractor implementation.
 *
 * Detailed CUE file introductions see https://github.com/libyal/libodraw/blob/master/documentation/CUE%20sheet%20format.asciidoc
 */
class CueInfo(cueInfoRoot: CueNode) {
    val node = cueInfoRoot
}