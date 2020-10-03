package com.shinonometn.media.cue

/**
 *
 * Defination of Cue Meta Fields
 *
 * Defined accoring to https://github.com/libyal/libodraw/blob/master/documentation/CUE%20sheet%20format.asciidoc
 *
 * */
enum class CueMetaFieldType(val directive: String) {

    //
    // Commands
    //

    /**
     * The CATALOG command is used to specify the "Media Catalog Number".
     * It will typically be used when mastering a CDROM for commercial production.
     *
     * The CATALOG command is defined as:
     *
     * CATALOG [media catalog number]
     *
     * The media catalog number must be a numeric value of 13 digits and encoded according
     * to UPC/EAN (Universal Product Code/European Article Number) rules.
     *
     * The CATALOG command can appear only once in the CUE sheet.
     * It will usually be the first command in the CUE sheet, but this is not mandatory.
     *
     */
    CATALOG("CATALOG"),

    /**
     * The CDTEXTFILE is used to specify the name of a file that contains the CD-TEXT information.
     *
     * CDTEXTFILE [file name]
     *
     * If the filename contains any spaces, then it must be enclosed in quotation marks.
     */
    CD_TEXT_FILE("CDTEXTFILE"),

    /**
     * The FILE command is used to specify a file that contains data.
     *
     * FILE [ filename ] [file type]
     *
     * If the filename contains any spaces, then it must be enclosed in quotation marks.
     * The first FILE command should be the commands in the CUE sheet with the exception of the CATALOG command.
     * */
    FILE("FILE"),

    /**
     * The REM command is used to specify remarks.
     *
     * REM [ remarks ]
     *
     * Note that the remarks also contain additional commands, see: Remarks commands.
     * */
    REMARK("REM"),

    /**
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
    TRACK("TRACK"),

    //
    // CD-Text commands
    //

    /**
     * The PERFORMER command is used to specify the name of a performer.
     *
     * PERFORMER [ string ]
     * If the string contains any spaces it must be enclosed in quotation marks.
     * Strings should be limited to a maximum of 80 characters.
     *
     * If the PERFORMER command appears before any TRACK commands it represents the performer of the entire disc.
     * If the command appears after a TRACK command it represents the performer of the current track.
     */
    PERFORMER("PERFORMER"),
    ALBUM_PERFORMER("ALBUMPERFORMER"),

    /**
     * The SONGWRITER command is used to specify the name of a song writer.
     *
     * SONGWRITER [ string ]
     * If the string contains any spaces it must be enclosed in quotation marks.
     * Strings should be limited to a maximum of 80 characters.
     *
     * If the SONGWRITER command appears before any TRACK commands it represents the song writer of the entire disc.
     * If the command appears after a TRACK command it represents the song writer of the current track.
     */
    SONG_WRITER("SONGWRITER"),
    ALBUM_SONG_WRITER("ALBUMSONGWRITER"),

    /**
     * The TITLE command is used to specify the name of a title.
     *
     * TITLE [ string ]
     * If the string contains any spaces it must be enclosed in quotation marks.
     * Strings should be limited to a maximum of 80 characters.
     *
     * If the TITLE command appears before any TRACK commands it represents the title of the entire disc.
     * If the command appears after a TRACK command it represents the title of the current track.
     */
    TITLE("TITLE"),
    TRACK_TITLE("TRACKTITLE"),

    //
    // Track commands
    //

    /**
     * The FLAGS command is used to define special sub-code flags for a track.
     *
     * FLAGS [flag types]
     * The flags types contain one or more track flags.
     *
     * The FLAGS command must appear after a TRACK command, but before any INDEX commands.
     * Only one FLAGS command is allowed per track.
     * */
    FLAGS("FALGS"),

    /**
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
    INDEX("INDEX"),

    /**
     * The ISRC command is used to specify the International Standard Recording Code (ISRC) of a track.
     * It will typically be used when mastering a CD for commercial production.
     *
     * ISRC [ISRC code]
     * The IRSC code must be 12 characters in length.
     * The first five characters are alphanumeric and the last seven are numeric.
     *
     * The ISRC command must be specified after a TRACK command, but before any INDEX commands.
     */
    ISRC("ISRC"),
    ISRC_CODE("ISRCCODE"),

    /**
     * The POSTGAP command is used to specify the length of a track post-gap.
     *
     * POSTGAP [ MSF ]
     * The post-gap data is considered not to be stored in the file specified by the FILE command.
     *
     * The POSTGAP command must appear after all INDEX commands for the current track. Only one POSTGAP command is allowed per track.
     *
     * See section: MSF
     * */
    POSTGAP("POSTGAP"),

    /**
     * The PREGAP command is used to specify the length of a track pre-gap.
     *
     * PREGAP [ MSF ]
     * The pre-gap data is considered not to be stored in the file specified by the FILE command.
     *
     * The PREGAP command must appear after a TRACK command, but before any INDEX commands. Only one PREGAP command is allowed per track.
     *
     * Also see section: MSF
     * */
    PREGAP("PREGAP"),

    //
    // Some undoucmented fields
    // found at "https://github.com/carlwilson/cuelib"
    //

    /**
     * Title of the album.
     */
    ALBUM_TITLE("ALBUMTITLE"),

    /**
     * An id for the disc. Typically the freedb disc id.
     */
    DISC_ID("DISCID"),

    /**
     * Album comment.
     */
    ALBUM_COMMENT("COMMENT"),

    /**
     * Genre of the album.
     */
    ALBUM_GENRE("GENRE"),

    /**
     * Number of a track.
     */
    TRACK_NUMBER("TRACKNUMBER"),

    /**
     * Performer of a track.
     */
    TRACK_PERFORMER("TRACKPERFORMER"),

    /**
     * Songwriter of a track.
     */
    TRACK_SONG_WRITER("TRACKSONGWRITER"),

    /**
     * Year of the album.
     */
    ALBUM_YEAR("YEAR"),

    /**
     * If directive unknown, return this
     * */
    UNKNOWN("UNKNOWN");

    companion object {
        private val reverseMap = (values().asList() indexToMap { i -> i.directive })

        fun byFieldName(fieldName: String) = reverseMap[fieldName] ?: UNKNOWN
    }
}