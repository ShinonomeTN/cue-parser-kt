package com.shinonometn.media.cue.parser

import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * Cue information tree node
 */
class CueTreeNode internal constructor(val type: CueTreeNodeType, val parent: CueTreeNode? = null) {
    internal val propertyMap by lazy { LinkedHashMap<String, String>() }
    private val _children by lazy { LinkedList<CueTreeNode>() }

    val properties: Map<String, String> = propertyMap
    val children: List<CueTreeNode> = _children

    internal fun createChildNode(type: CueTreeNodeType): CueTreeNode {
        if (!isChildTypeAllowed(type)) error("${this.type.name} cannot have ${type.name} as children.")
        val child = CueTreeNode(type, this)
        _children.add(child)
        return child
    }

    internal fun isChildTypeAllowed(childType: CueTreeNodeType): Boolean {
        return type.allowedChildren.contains(childType)
    }

    override fun toString(): String {
        return "CueNode(type=$type, properties=$properties, children=$children)"
    }
}