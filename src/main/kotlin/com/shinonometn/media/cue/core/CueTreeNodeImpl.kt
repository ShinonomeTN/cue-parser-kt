package com.shinonometn.media.cue.core

import java.util.*

/**
 * Cue information tree node
 */
class CueTreeNodeImpl internal constructor(
    override val type: CueTreeNodeType,
    override val parent: CueTreeNodeImpl? = null
) : MutableCueTreeNode {
    override val properties: MutableMap<String, String> by lazy { LinkedHashMap<String, String>() }
    override val children: MutableList<CueTreeNodeImpl> by lazy { LinkedList<CueTreeNodeImpl>() }

    override fun createChildNode(type: CueTreeNodeType): CueTreeNodeImpl {
        if (!isChildTypeAllowed(type)) error("${this.type.name} cannot have ${type.name} as children.")
        val child = CueTreeNodeImpl(type, this)
        children.add(child)
        return child
    }

    override fun removeChildNode(node: CueTreeNode) {
        children.removeIf { node == it }
    }

    override fun removeChildNode(filter: (CueTreeNode) -> Boolean) {
        children.removeIf(filter)
    }

    internal fun isChildTypeAllowed(childType: CueTreeNodeType): Boolean {
        return type.allowedChildren.contains(childType)
    }

    override fun toString(): String {
        return "CueNode(type=$type, properties=$properties, children=$children)"
    }
}