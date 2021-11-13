package com.shinonometn.media.cue.reader

/**
 * Read CUE album info by using provided Key (@see [AlbumInfo])
 */
fun CueInfoReader.albumInfo(albumInfo: AlbumInfo) : String? {
    return with(albumInfo) { extractor(rootNode.properties) }
}

open class AlbumInfo internal constructor(
    vararg keys : String, extractor : Extractor
) : MetaPropertyReader(*keys, extractor = extractor) {

    /**
     * About CUE CATALOG command
     *
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
    object Catalog : AlbumInfo("catalog", extractor = EXTRACTOR_DEFAULT)

    /**
     * About CUE CDTEXTFILE command
     *
     * The CDTEXTFILE is used to specify the name of a file that contains the CD-TEXT information.
     *
     * CDTEXTFILE [file name]
     *
     * If the filename contains any spaces, then it must be enclosed in quotation marks.
     */
    object CDTextFile : AlbumInfo("cdtextfile", extractor = EXTRACTOR_DEFAULT)

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
    object Performer : AlbumInfo("performer", "albumperformer", extractor = EXTRACTOR_PREFER_FIRST)
//    object AlbumPerformer : AlbumInfo("albumperformer", extractor = EXTRACTOR_PREFER_FIRST)

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
    object SongWriter : AlbumInfo("songwriter", "albumsongwriter", extractor = EXTRACTOR_PREFER_FIRST)
//    object AlbumSongWriter : AlbumInfo("albumsongwriter", extractor = EXTRACTOR_PREFER_FIRST)

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
    object Title : AlbumInfo("title", "albumtitle", extractor = EXTRACTOR_PREFER_FIRST)
//    object AlbumTitle : AlbumInfo("albumtitle", "title", extractor = EXTRACTOR_PREFER_FIRST)

    /**
     * An id for the disc. Typically the freedb disc id.
     */
    object DiscID : AlbumInfo("discid", extractor = EXTRACTOR_DEFAULT)

    /**
     * Genre of the album.
     */
    object Genre : AlbumInfo("genre", extractor = EXTRACTOR_DEFAULT)

    /**
     * Year of the album.
     * Get the exact 'YEAR' value.
     */
//    object Year: AlbumInfo("year", extractor = EXTRACTOR_DEFAULT)

    /**
     * Release date of the album.
     * If no DATE present, use YEAR.
     */
    object Date: AlbumInfo("date", "year", extractor = EXTRACTOR_PREFER_FIRST)

//    TRACK_TITLE("TRACKTITLE"),
}