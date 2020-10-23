package com.shinonometn.media.cue.parser

//
// CueNodeType
//
private typealias CueChildTypeProvider = () -> Set<CueNodeType>

private typealias CueDirectiveHandler = CueNodeType.(node: CueNode, arguments: CueArgument, lineNumber: Int) -> CueNode

private fun CueNodeType.reclusiveFindContainerNode(node: CueNode): CueNode = if (!node.isChildTypeAllowed(this)) {
    var current: CueNode = node
    while (current.type != this) {
        current = current.parent ?: error("Unexpected reached root node, type ${current.type.name}")
    }
    current.parent ?: error("Unexpected reached root node, type ${current.type.name}")
} else node

/**
 * Basic cue directives
 * Some of those directives will change the hierarchy of cue information tree:
 * - FILE : Creating a FILE sub-tree contains all tracks
 * - TRACK: Creating a TRACK node containing track info
 *
 * Others will just write meta info on the node
 */
enum class CueNodeType(
    private val childNodeTypes: CueChildTypeProvider,
    internal val directiveHandler: CueDirectiveHandler
) {
    ROOT(
        childNodeTypes = {
            setOf(
                META,
                FILE
            )
        },
        directiveHandler = { node, _, _ -> node }
    ),

    /**
     * About CUE FILE command:
     *
     * The FILE command is used to specify a file that contains data.
     *
     * FILE [ filename ] [file type]
     *
     * If the filename contains any spaces, then it must be enclosed in quotation marks.
     * The first FILE command should be the commands in the CUE sheet with the exception of the CATALOG command.
     * */
    FILE(
        childNodeTypes = { setOf(TRACK) },
        directiveHandler = { node, argument, line ->
            reclusiveFindContainerNode(node).createChildNode(this).apply {
                val parameters = argument.getParameters().takeIf { it.size >= 2 }
                    ?: error("Line $line: FILE should have 2 parameters")

                propertyMap[CUE_PROPERTY_FILE_NAME] = parameters[0]
                propertyMap[CUE_PROPERTY_FILE_TYPE] = parameters[1]
            }
        }
    ),

    /**
     * About CUE TRACK command
     *
     * The TRACK command is used to specify a track.
     *
     * TRACK [track number] [track type]
     *
     * The track number should be a number ranging from 1 to 99.
     * It is unclear of the index number should always be represented by 2 digits.
     *
     * The first track number can be greater than one, but all track numbers after the first must be sequential.
     * The CUE sheet should at least contain 1 TRACK command.
     *
     * Note: An unspecified gap between tracks is allowed.
     * */
    TRACK(
        childNodeTypes = { setOf(META) },
        directiveHandler = { node, argument, line ->
            reclusiveFindContainerNode(node).createChildNode(this).apply {
                val parameters = argument.getParameters().takeIf { it.size >= 2 }
                    ?: error("Line $line: TRACK should have 2 parameters.")

                propertyMap[CUE_PROPERTY_TRACK_NUMBER] = parameters[0]
                propertyMap[CUE_PROPERTY_TRACK_TYPE] = parameters[1]
            }
        }
    ),

    /**
     * About CUE INDEX command
     *
     * The INDEX command is used to specify indexes or sub-indexes of the track.
     *
     * INDEX [index number] [ MSF ]
     * The index number should be a number ranging from 0 to 99.
     * It is unclear of the index number should always be represented by 2 digits.
     *
     * The index number have the following meaning:
     *
     * 0 specifies the pre-gap of the track;
     *
     * 1 specifies the start of the track;
     *
     * 2 - 99 specify a sub-index within the track.
     *
     * See section: MSF
     * */
    INDEX(
        childNodeTypes = { emptySet() },
        directiveHandler = { node, argument, line ->
            node.apply {
                val parameters = argument.getParameters().takeIf { it.size >= 2 }
                    ?: error("Line $line: INDEX should have 2 parameters.")
                propertyMap["$CUE_PROPERTY_TRACK_INDEX_PREFIX${parameters[0]}"] = parameters[1]
            }
        }
    ),

    /**
     * Commands that doesn't change information tree's hierarchy are
     * considered as meta info.
     */
    META(
        childNodeTypes = { emptySet() },
        directiveHandler = { node, argument, _ ->
            node.apply {
                val command = argument.getDirective()
                val parameters = argument.getParameters().takeIf { it.isNotEmpty() } ?: return@apply

                propertyMap[command.toLowerCase()] = parameters.joinToString(separator = " ") {
                    if (it.contains(' ')) """"$it"""" else it
                }
            }
        }
    ),


    /**
     * About CUE REM command
     *
     * The REM command is used to specify remarks.
     *
     * REM [ remarks ]
     *
     * Note that the remarks also contain additional commands, see: Remarks commands.
     * */
    REM(
        childNodeTypes = { emptySet() },
        directiveHandler = { node, argument, line ->
            node.apply {
                val parameters = argument.getParameters().takeIf { it.isNotEmpty() } ?: error("Line $line: Empty REM.")
                val remDirective = remCommandMap[parameters[0]] ?: META
                val handler = remDirective.directiveHandler

                handler(remDirective, node, parameters, line)
            }
        }
    ),

    REMARKED_COMMENT(
        childNodeTypes = { emptySet() },
        directiveHandler = { node, argument, line ->
            node.apply {
                val comment = argument.getParameters().takeIf { it.isNotEmpty() }?.get(0) ?: ""
                propertyMap["$CUE_PROPERTY_REM_COMMENT_PREFIX$line"] = comment
            }
        }
    ),

    COMMENT(
        childNodeTypes = { emptySet() },
        directiveHandler = { node, argument, line ->
            node.apply {
                val comment = argument.getParameters().takeIf { it.isNotEmpty() }?.get(0) ?: ""
                propertyMap["$CUE_PROPERTY_COMMENT_PREFIX$line"] = comment
            }
        }
    );

    val allowedChildren: Set<CueNodeType> by lazy { childNodeTypes() }
}

//
// Main Commands
//
internal val commandMap = mapOf(
    ";" to CueNodeType.COMMENT,
    "FILE" to CueNodeType.FILE,
    "TRACK" to CueNodeType.TRACK,
    "REM" to CueNodeType.REM,
    "INDEX" to CueNodeType.INDEX
)

//
// REM Commands
//
internal val remCommandMap = mapOf(
    "COMMENT" to CueNodeType.REMARKED_COMMENT
)