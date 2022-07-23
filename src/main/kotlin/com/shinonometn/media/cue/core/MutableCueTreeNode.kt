package com.shinonometn.media.cue.core

interface MutableCueTreeNode : CueTreeNode {
    override val properties: MutableMap<String, String>

    override val parent: MutableCueTreeNode?

    fun createChildNode(type: CueTreeNodeType): MutableCueTreeNode
    fun removeChildNode(node: CueTreeNode)
    fun removeChildNode(filter: (CueTreeNode) -> Boolean)
}