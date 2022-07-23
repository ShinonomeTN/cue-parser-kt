package com.shinonometn.media.cue.core

import com.shinonometn.media.cue.parser.*

//
// CueNodeType
//
private typealias CueChildTypeProvider = () -> Set<CueTreeNodeType>

private typealias CueDirectiveHandler = CueTreeNodeType.(node: CueTreeNodeImpl, arguments: CueArgument, lineNumber: Int) -> CueTreeNodeImpl

private fun CueTreeNodeType.reclusiveFindContainerNode(container: CueTreeNodeImpl): CueTreeNodeImpl = if (container.isChildTypeAllowed(this)) container else {
    var parent: CueTreeNodeImpl = container.parent ?: error("No container for type '${container.type.name}', append to type '$name'")
    while (!parent.isChildTypeAllowed(this)) {
        parent = parent.parent ?: error("Could not find container for type '${parent.type.name}', append to type '${parent.type.name}'")
    }
    parent
}

private fun MutableMap<String, String>.setArguments(parameters: List<String>) {
    putAll(parameters.mapIndexed { index, s -> index.toString() to s })
}

private fun newNodeDirectiveHandler(minParameterCount: Int): CueDirectiveHandler = { node, argument, line ->
    val parameters = argument.getParameters().takeIf { it.size >= minParameterCount }
        ?: error("Line $line: $name directive requires at least $minParameterCount parameter${if (minParameterCount <= 1) "" else "s"}.")

    reclusiveFindContainerNode(node).createChildNode(this).apply {
        if (type == CueTreeNodeType.META) properties["@"] = argument.getDirective()
        properties.setArguments(parameters)
    }
}

private fun noopDirectiveHandler(): CueDirectiveHandler = { node, _, _ -> node }

private fun wrapIfNecessary(value: String): String {
    return if (value.contains(" ")) "\"$value\"" else value
}

private fun Map<String,String>.toSortedArgumentList() : List<String> {
    return mapNotNull { it.key.toIntOrNull()?.let { index -> index to it.value } }.sortedBy { it.first }.map { it.second }
}

/**
 * Basic cue directives
 * Some of those directives will change the hierarchy of cue information tree:
 * - FILE : Creating a FILE sub-tree contains all tracks
 * - TRACK: Creating a TRACK node containing track info
 *
 * Others will just write meta info on the node
 */
enum class CueTreeNodeType(
    val childNodeTypes: CueChildTypeProvider,
    val directiveHandler: CueDirectiveHandler,
    private val directiveTemplate: (Map<String, String>) -> String
) {
    ROOT(
        childNodeTypes = {
            setOf(
                REM,
                META,
                FILE,
                COMMENT
            )
        },
        directiveHandler = noopDirectiveHandler(),
        directiveTemplate = {
            """"""
        }
    ),


    /**
     * Description: Sets a new input file.
     *
     * Section(s): global
     *
     * Format: `FILE <filename> [file_format]`
     * - "filename": a string.
     * - "file_format": one of: "BINARY", "MOTOROLA", "AIFF", "WAVE", or "MP3"
     *
     * "FILE" line precedes the beginning of the track section.
     *
     * **About CUE FILE command :**
     *
     * The FILE command is used to specify a file that contains data.
     *
     * If the filename contains any spaces, then it must be enclosed in quotation marks.
     * The first FILE command should be the commands in the CUE sheet with the exception of the CATALOG command.
     *
     */
    FILE(
        childNodeTypes = { setOf(TRACK, REM, COMMENT) },
        directiveHandler = newNodeDirectiveHandler(1),
        directiveTemplate = {
            """
            FILE ${wrapIfNecessary(it["0"] ?: "")} ${wrapIfNecessary(it["1"] ?: "")}
            """
        }
    ),

    /**
     * Description: Starts a new track.
     *
     * Section(s): track
     *
     * Format: `TRACK <track_number> <track_mode>`
     * - track_number: a positive integer in the range 1-99.
     * - track_mode: one of: "AUDIO", "CDG", "MODE1/2048", "MODE1/2352", "MODE2/2336", "MODE2/2352", "CDI/2336", or "CDI/2352".
     *
     * **About CUE TRACK command :**
     *
     * The TRACK command is used to specify a track.
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
        childNodeTypes = { setOf(REM, INDEX, META, COMMENT) },
        directiveHandler = newNodeDirectiveHandler(2),
        directiveTemplate = {
            """
            TRACK ${wrapIfNecessary(it["0"] ?: "")} ${wrapIfNecessary(it["1"] ?: "")}
            """
        }
    ),

    /**
     * Description: Sets a track index.
     *
     * Section(s): track
     *
     * Format: `INDEX <index_number> <index>`
     * - index_number: an integer in the range 0-99.
     * - index: time in MSF format.
     *
     * Having an "INDEX 00" and an "INDEX 01" defines a pregap using data from the "FILE".
     *
     * **About CUE INDEX command :**
     *
     * The INDEX command is used to specify indexes or sub-indexes of the track.
     *
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
        directiveHandler = newNodeDirectiveHandler(2),
        directiveTemplate = {
            """
            INDEX ${wrapIfNecessary(it["0"] ?: "")} ${wrapIfNecessary(it["1"] ?: "")}
            """
        }
    ),

    /**
     * Commands that doesn't change information tree's hierarchy are
     * considered as meta info.
     */
    META(
        childNodeTypes = { emptySet() },
        directiveHandler = newNodeDirectiveHandler(0),
        directiveTemplate = { props ->
            """
            ${props["@"] ?: ""} ${props.toSortedArgumentList().joinToString(" ") { wrapIfNecessary(it) }}
            """
        }
    ),


    /**
     * Description: Begins a comment line.
     * Section(s): any
     * Format: `REM <comments> [arguments]`
     *
     * **About CUE REM command: **
     *
     * The REM command is used to specify remarks.
     *
     * REM [ remarks ]
     *
     * Note that the remarks also contain additional commands, see: Remarks commands.
     * */
    REM(
        childNodeTypes = { emptySet() },
        directiveHandler = newNodeDirectiveHandler(1),
        directiveTemplate = {
            """
            REM ${it.toSortedArgumentList().joinToString(" ") { arg -> wrapIfNecessary(arg) }}
            """
        }
    ),

    COMMENT(
        childNodeTypes = { emptySet() },
        directiveHandler = newNodeDirectiveHandler(0),
        directiveTemplate = {
            """
            ; ${it.toSortedArgumentList().joinToString(" ")}
            """
        }
    );

    val allowedChildren: Set<CueTreeNodeType> by lazy { childNodeTypes() }

    fun toLine(properties : Map<String, String>) : String {
        return directiveTemplate(properties).trim()
    }

    companion object {
        private val literalCommandBlackList = setOf(ROOT, COMMENT, META)
        val literalCommandMappings = mapOf(
            ";" to COMMENT,
            *values().filter { it !in literalCommandBlackList }.map { it.name to it }.toTypedArray()
        )
    }
}