package com.shinonometn.media.cue.parser

import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * Cue information tree node
 */
class CueNode internal constructor(val type: CueNodeType, val parent: CueNode? = null) {
    internal val propertyMap by lazy { LinkedHashMap<String, String>() }
    private val _children by lazy { LinkedList<CueNode>() }

    val properties: Map<String, String> = propertyMap
    val children: List<CueNode> = _children

    internal fun createChildNode(type: CueNodeType): CueNode {
        if (!isChildTypeAllowed(type)) error("${this.type.name} cannot have ${type.name} as children.")
        val child = CueNode(type, this)
        _children.add(child)
        return child
    }

    internal fun isChildTypeAllowed(childType: CueNodeType): Boolean {
        return type.allowedChildren.contains(childType)
    }

    override fun toString(): String {
        return "CueNode(type=$type, properties=$properties, children=$children)"
    }
}