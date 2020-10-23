package com.shinonometn.media.cue.reader

import com.shinonometn.media.cue.parser.CUE_PROPERTY_COMMENT_PREFIX
import com.shinonometn.media.cue.parser.CUE_PROPERTY_REM_COMMENT_PREFIX
import com.shinonometn.media.cue.parser.CueNode

enum class CueCommentTypes(internal val prefix: String) {
    FILE_COMMENTS(CUE_PROPERTY_COMMENT_PREFIX),
    REMARK_COMMENTS(CUE_PROPERTY_REM_COMMENT_PREFIX);
}

fun CueNode.getComments(type: CueCommentTypes): List<CueComment> {
    val prefix = type.prefix
    return properties.filterKeys { it.startsWith(prefix) }.map {
        val line = it.key.removePrefix(prefix)
        CueComment(line.toInt(), it.value, type)
    }
}

/**
 * Cue comments have two types, one is starts with ';' and
 * other one using command "REM COMMENT"
 */
class CueComment(line: Int, content: String, type: CueCommentTypes)