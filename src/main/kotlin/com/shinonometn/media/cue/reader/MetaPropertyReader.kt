package com.shinonometn.media.cue.reader

import com.shinonometn.media.cue.core.CueTreeNode
import com.shinonometn.media.cue.core.CueTreeNodeType

internal typealias Extractor = MetaPropertyReader.(CueTreeNode) -> String?

open class MetaPropertyReader constructor(vararg keys: String, val extractor: Extractor) {
    val keys = keys.map { it.toUpperCase() }.toSet()

    companion object {
        val DefaultExtractor = EXTRACTOR_DEFAULT
        val PreferFirstExtractor = EXTRACTOR_PREFER_FIRST
        val PreferFirstRecursivelyExtractor = EXTRACTOR_RECURSIVELY_PREFER_FIRST
    }
}

internal val EXTRACTOR_DEFAULT : Extractor = { node ->
    node.children.firstOrNull {
        it.type == CueTreeNodeType.META && it.directive in keys
    }?.let { it.properties["0"] }
}

internal val EXTRACTOR_PREFER_FIRST : Extractor = { node ->
    node.children.firstOrNull {
        it.type == CueTreeNodeType.META && it.directive in keys
    }?.let { it.properties["0"] }
}

private fun walkCueNodeTree(keys : Set<String>, node : CueTreeNode) : String? {
    if(node.type == CueTreeNodeType.META && node.directive in keys) {
        val value = node.properties["0"]
        if(value != null) return value
    }

    for (child in node.children) {
        val value = walkCueNodeTree(keys, child)
        if(value != null) return value
    }

    return null
}

internal val EXTRACTOR_RECURSIVELY_PREFER_FIRST : Extractor = { node ->
    walkCueNodeTree(keys, node)
}