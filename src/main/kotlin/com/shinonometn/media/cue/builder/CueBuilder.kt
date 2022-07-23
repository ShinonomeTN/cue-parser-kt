package com.shinonometn.media.cue.builder

import com.shinonometn.media.cue.core.*

operator fun <T : CueBaseBuilderContext> T.invoke(block: T.() -> Unit): T {
    block()
    return this
}

class CueBuilderContextImpl(
    private val root: MutableCueTreeNode = CueTreeNodeImpl(CueTreeNodeType.ROOT)
) : CueRootBuilderContext, CueFileBuilderContext, CueTrackBuilderContext {

    val properties = root.properties

    val rootNode: CueTreeNode
        get() = root

    override fun comment(comment: String): MutableCueTreeNode {
        return root.createChildNode(CueTreeNodeType.COMMENT).apply {
            properties["0"] = comment
        }
    }

    override fun meta(vararg args: String): MutableCueTreeNode {
        require(args.size >= 2) { "meta needs at least 2 arguments" }
        return root.createChildNode(CueTreeNodeType.META).apply {
            val directive = args[0]
            val arguments = args.drop(1)
            properties["@"] = directive
            properties.putAll(arguments.mapIndexed { index, value -> index.toString() to value })
        }
    }

    override fun rem(vararg args: String): MutableCueTreeNode {
        require(args.isNotEmpty()) { "rem needs at least 1 argument" }
        return root.createChildNode(CueTreeNodeType.REM).apply {
            properties.putAll(args.drop(1).mapIndexed { index, value -> index.toString() to value })
        }
    }

    override fun file(filename: String, file_format: String?, builder: CueFileBuilderContext.() -> Unit): CueFileBuilderContext {
        return CueBuilderContextImpl(root.createChildNode(CueTreeNodeType.FILE).apply {
            properties["0"] = filename
            file_format?.let { properties["1"] = it }
        }).apply(builder)
    }

    override fun track(number: Int, mode: String, builder: CueTrackBuilderContext.() -> Unit): CueTrackBuilderContext {
        return CueBuilderContextImpl(root.createChildNode(CueTreeNodeType.TRACK).apply {
            properties["0"] = number.toString().padStart(2, '0')
            properties["1"] = mode
        }).apply(builder)
    }

    override fun index(number: Int, msf: MSFTimePoint): MutableCueTreeNode {
        return root.createChildNode(CueTreeNodeType.INDEX).apply {
            properties["0"] = number.toString().padStart(2, '0')
            properties["1"] = msf.format()
        }
    }

    override fun build(): String = root.buildCueSheet()
}

private fun buildCueSheet(root: CueTreeNode, sb: StringBuilder) {
    sb.appendln(root.type.toLine(root.properties))
    if (root.children.isNotEmpty()) root.children.forEach { buildCueSheet(it, sb) }
}

fun CueTreeNode.buildCueSheet(): String {
    val sb = StringBuilder()
    buildCueSheet(this, sb)
    return sb.toString()
}

/**
 * Build cue sheet
 */
@Suppress("FunctionName")
@CueBuilderDsl
fun CueSheet(builder: CueRootBuilderContext.() -> Unit): CueRootBuilderContext {
    val context = CueBuilderContextImpl()
    context.builder()
    return context
}