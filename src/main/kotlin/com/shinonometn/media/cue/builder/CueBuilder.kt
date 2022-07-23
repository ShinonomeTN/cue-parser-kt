package com.shinonometn.media.cue.builder

import com.shinonometn.media.cue.core.*

@DslMarker
annotation class CueBuilderDsl

interface CueBaseBuilderContext {
    @CueBuilderDsl
    fun rem(vararg args : String) : MutableCueTreeNode

    @CueBuilderDsl
    fun comment(comment : String) : MutableCueTreeNode
}

operator fun <T : CueBaseBuilderContext> T.invoke(block : T.() -> Unit) : T {
    block()
    return this
}

interface CueRootBuilderContext : CueBaseBuilderContext {
    @CueBuilderDsl
    fun meta(vararg args : String) : MutableCueTreeNode

    /**
     * Description: Sets the catalog number of the CD.
     *
     * Section(s): global
     *
     * Format:
     * ```
     *  CATALOG <catalog_number>
     * ```
     * - catalog_number: an integer consisting of exactly 13 digits.
     *
     */
    fun catalog(number : Int) : MutableCueTreeNode

    /**
     * Description: Sets an external file for CD-TEXT data.
     *
     * Section(s): global
     *
     * Format:
     * ```
     * CDTEXTFILE <filename>
     * ```
     * - filename: a string.
     *
     * The CD-TEXT file contains encoded CD-TEXT data. The standard extension for CD-TEXT files is ".cdt".
     */
    @CueBuilderDsl
    fun cdtextfile(file : String) : MutableCueTreeNode

    /**
     * Description: Sets a new input file.
     *
     * Section(s): global
     *
     * Format: `FILE <filename> [file_format]`
     * - filename: a string.
     * - file_format: one of: "BINARY", "MOTOROLA", "AIFF", "WAVE", or "MP3"
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
    @CueBuilderDsl
    fun file(filename : String, file_format : String? = null, builder : CueFileBuilderContext.() -> Unit = {}) : CueFileBuilderContext

    fun build() : String
}

interface CueFileBuilderContext : CueBaseBuilderContext {
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
    @CueBuilderDsl
    fun track(number : Int, mode : String, builder : CueTrackBuilderContext.() -> Unit = {}) : CueTrackBuilderContext
}

interface CueTrackBuilderContext : CueBaseBuilderContext {

    /**
     * Description: Sets track flags.
     *
     * Section(s): track
     *
     * Format: `FLAGS [flag]...`
     * - flag: one of: "PRE", "DCP", "4CH", or "SCMS".
     */
    @CueBuilderDsl
    fun flags(vararg flag : String) : MutableCueTreeNode

    /**
     * Description: Sets track ISRC number.
     *
     * Section(s): track
     *
     * Format: `ISRC <ISRC_number>`
     * - ISRC_number: a string with the format CCOOOOYYSSSSS.
     */
    @CueBuilderDsl
    fun isrc(isrc : String) : MutableCueTreeNode

    /**
     * Description: Sets track postgap.
     *
     * Section(s): track
     *
     * Format: `POSTGAP <postgap>`
     * - postgap: time in MSF format.
     */
    @CueBuilderDsl
    fun postgap(msf : MSFTimePoint) : MutableCueTreeNode

    /**
     * Description: Sets track pregap.
     *
     * Section(s): track
     *
     * Format: `PREGAP <pregap>`
     * - pregap: time in MSF format.
     *
     * The pregap is filled with generated silence.
     * */
    @CueBuilderDsl
    fun pregap(msf: MSFTimePoint) : MutableCueTreeNode

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
    @CueBuilderDsl
    fun index(number : Int, msf : MSFTimePoint) : MutableCueTreeNode
}

class CueBuilderContext(private val root : MutableCueTreeNode = CueTreeNodeImpl(CueTreeNodeType.ROOT))
    : CueRootBuilderContext, CueFileBuilderContext, CueTrackBuilderContext {

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

    override fun catalog(number: Int): MutableCueTreeNode {
        val catalogNumber = number.toString().padStart(13, '0')
        require(catalogNumber.length == 13) { "catalog number must be exactly 13 digits" }
        return meta("CATALOG", catalogNumber)
    }

    override fun cdtextfile(file: String): MutableCueTreeNode {
        return meta("CDTEXTFILE", file)
    }

    override fun rem(vararg args: String): MutableCueTreeNode {
        require(args.isNotEmpty()) { "rem needs at least 1 argument" }
        return root.createChildNode(CueTreeNodeType.REM).apply {
            properties.putAll(args.drop(1).mapIndexed { index, value -> index.toString() to value })
        }
    }

    override fun file(filename: String, file_format: String?, builder : CueFileBuilderContext.() -> Unit): CueFileBuilderContext {
        return CueBuilderContext(root.createChildNode(CueTreeNodeType.FILE).apply {
            properties["0"] = filename
            file_format?.let { properties["1"] = it }
        }).apply(builder)
    }

    override fun track(number: Int, mode: String, builder : CueTrackBuilderContext.() -> Unit): CueTrackBuilderContext {
        return CueBuilderContext(root.createChildNode(CueTreeNodeType.TRACK).apply {
            properties["0"] = number.toString().padStart(2, '0')
            properties["1"] = mode
        }).apply(builder)
    }

    override fun flags(vararg flag: String): MutableCueTreeNode {
        return meta("FLAGS", *flag)
    }

    override fun isrc(isrc: String): MutableCueTreeNode {
        val s = isrc.padStart(12, '0')
        // CC OOOO YY SSSSS
        require(s.length == 12) { "ISRC number must be exactly 12 digits" }
        return meta("ISRC", s)
    }

    override fun postgap(msf: MSFTimePoint): MutableCueTreeNode {
        return meta("POSTGAP", msf.format())
    }

    override fun pregap(msf: MSFTimePoint): MutableCueTreeNode {
        return meta("PREGAP", msf.format())
    }

    override fun index(number: Int, msf: MSFTimePoint): MutableCueTreeNode {
        return root.createChildNode(CueTreeNodeType.INDEX).apply {
            properties["0"] = number.toString().padStart(2, '0')
            properties["1"] = msf.format()
        }
    }

    private fun findRoot(current : MutableCueTreeNode) : MutableCueTreeNode {
        return when (val parent = current.parent) {
            null -> current
            else -> findRoot(parent)
        }
    }

    val properties = root.properties

    override fun build() : String = root.buildCueSheet()

    fun getRootNode() : CueTreeNode = root
}

private fun buildCueSheet(root : CueTreeNode, sb : StringBuilder) {
    sb.appendln(root.type.toLine(root.properties))
    if(root.children.isNotEmpty()) root.children.forEach { buildCueSheet(it, sb) }
}

fun CueTreeNode.buildCueSheet() : String {
    val sb = StringBuilder()
    buildCueSheet(this, sb)
    return sb.toString()
}

/**
 * Build cue sheet
 */
@Suppress("FunctionName")
@CueBuilderDsl
fun CueSheet(builder : CueRootBuilderContext.() -> Unit) : CueRootBuilderContext {
    val context = CueBuilderContext()
    context.builder()
    return context
}