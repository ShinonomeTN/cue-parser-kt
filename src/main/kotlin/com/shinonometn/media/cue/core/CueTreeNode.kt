package com.shinonometn.media.cue.core

interface CueTreeNode {
    val type: CueTreeNodeType
    val parent: CueTreeNode?

    val properties: Map<String, String>
    val children: List<CueTreeNode>

    val arguments : List<String>
        get() = properties.mapNotNull { (key, value) -> key.toIntOrNull()?.let { it to value } }.sortedBy { it.first }.map { it.second }

    val directive : String
        get() = (properties["@"] ?: type.name).toUpperCase()
}