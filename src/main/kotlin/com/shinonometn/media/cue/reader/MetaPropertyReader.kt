package com.shinonometn.media.cue.reader

import com.shinonometn.media.cue.core.CueTreeNode
import com.shinonometn.media.cue.core.CueTreeNodeType

internal typealias Extractor = MetaPropertyReader.(CueTreeNode) -> String?

open class MetaPropertyReader constructor(vararg keys: String, val extractor: Extractor) {
    val keys = keys.map { it.toUpperCase() }.toSet()

    companion object {
        /**
         * Returns first key hits
         */
        val DefaultExtractor = EXTRACTOR_DEFAULT

        /**
         * Returns first key hits, but including children values
         */
        val PreferFirstRecursivelyExtractor = EXTRACTOR_RECURSIVELY_PREFER_FIRST
    }
}

private fun searchKeyIncludeREM(keys : Set<String>, node : CueTreeNode) : String? {
    return node.children.firstOrNull { it.type == CueTreeNodeType.META && it.directive in keys }?.let { it.properties["0"] }
        ?: node.children.firstOrNull { it.type == CueTreeNodeType.REM && it.properties["0"] in keys }?.let { it.properties["1"] }
}

internal val EXTRACTOR_DEFAULT : Extractor = { node ->
    searchKeyIncludeREM(keys, node)
}

private fun walkCueNodeTree(keys : Set<String>, node : CueTreeNode) : String? {
    val value = searchKeyIncludeREM(keys, node)
    if(value != null) return value

    for (child in node.children) {
        val childValue = walkCueNodeTree(keys, child)
        if(childValue != null) return childValue
    }

    return null
}

internal val EXTRACTOR_RECURSIVELY_PREFER_FIRST : Extractor = { node ->
    walkCueNodeTree(keys, node)
}