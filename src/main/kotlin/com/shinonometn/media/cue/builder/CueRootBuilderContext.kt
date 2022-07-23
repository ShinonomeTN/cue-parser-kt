package com.shinonometn.media.cue.builder

import com.shinonometn.media.cue.core.MutableCueTreeNode

interface CueRootBuilderContext : CueAlbumInfoBuilderContext {

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
    fun catalog(number : Int) : MutableCueTreeNode {
        val catalogNumber = number.toString().padStart(13, '0')
        require(catalogNumber.length == 13) { "catalog number must be exactly 13 digits" }
        return meta("CATALOG", catalogNumber)
    }

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
    fun cdtextfile(file : String) : MutableCueTreeNode {
        return meta("CDTEXTFILE", file)
    }

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