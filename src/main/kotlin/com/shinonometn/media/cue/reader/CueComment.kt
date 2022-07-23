package com.shinonometn.media.cue.reader

import com.shinonometn.media.cue.core.CueTreeNode
import com.shinonometn.media.cue.core.CueTreeNodeType

enum class CueCommentTypes {
    FILE_COMMENTS,
    REMARK_COMMENTS;
}

fun CueTreeNode.comments(type: CueCommentTypes): List<CueComment> {
    return when (type) {
        CueCommentTypes.FILE_COMMENTS -> children.filter {
            it.type == CueTreeNodeType.COMMENT
        }.map { node ->
            val comment = node.arguments.joinToString(" ")
            CueComment(comment, CueCommentTypes.FILE_COMMENTS)
        }

        CueCommentTypes.REMARK_COMMENTS -> children.filter {
            it.type == CueTreeNodeType.REM && it.directive == "COMMENT"
        }.map { node ->
            val comment = node.arguments.joinToString(" ")
            CueComment(comment, CueCommentTypes.REMARK_COMMENTS)
        }
    }
}

/**
 * Cue comments have two types, one is starts with ';' and
 * other one using command "REM COMMENT"
 */
class CueComment(val content: String, val type: CueCommentTypes)