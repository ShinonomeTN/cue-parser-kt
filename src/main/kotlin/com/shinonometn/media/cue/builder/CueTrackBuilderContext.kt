package com.shinonometn.media.cue.builder

import com.shinonometn.media.cue.core.MSFTimePoint
import com.shinonometn.media.cue.core.MutableCueTreeNode

interface CueTrackBuilderContext : CuePerformerInfoBuilderContext {

    /**
     * Description: Sets track flags.
     *
     * Section(s): track
     *
     * Format: `FLAGS [flag]...`
     * - flag: one of: "PRE", "DCP", "4CH", or "SCMS".
     */
    @CueBuilderDsl
    fun flags(vararg flag : String) : MutableCueTreeNode {
        return meta("FLAGS", *flag)
    }

    /**
     * Description: Sets track ISRC number.
     *
     * Section(s): track
     *
     * Format: `ISRC <ISRC_number>`
     * - ISRC_number: a string with the format CCOOOOYYSSSSS.
     */
    @CueBuilderDsl
    fun isrc(isrc : String) : MutableCueTreeNode {
        val s = isrc.padStart(12, '0')
        // CC OOOO YY SSSSS
        require(s.length == 12) { "ISRC number must be exactly 12 digits" }
        return meta("ISRC", s)
    }

    /**
     * Description: Sets track postgap.
     *
     * Section(s): track
     *
     * Format: `POSTGAP <postgap>`
     * - postgap: time in MSF format.
     */
    @CueBuilderDsl
    fun postgap(msf : MSFTimePoint) : MutableCueTreeNode {
        return meta("POSTGAP", msf.format())
    }

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
    fun pregap(msf: MSFTimePoint) : MutableCueTreeNode {
        return meta("PREGAP", msf.format())
    }

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