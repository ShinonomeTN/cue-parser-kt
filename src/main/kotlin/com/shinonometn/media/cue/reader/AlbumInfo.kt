package com.shinonometn.media.cue.reader

import com.shinonometn.media.cue.CueInfo

typealias AlbumInfoExtractor = AlbumInfo.(Map<String,String>) -> String?

private val EXTRACTOR_DEFAULT : AlbumInfoExtractor = {
    it[keys[0]]
}

private val EXTRACTOR_PREFER_FIRST : AlbumInfoExtractor = {
    var content : String? = null
    for(key in keys) {
        content = it[key]
        if(content != null) break
    }
    content
}

fun CueInfo.getInfo(albumInfo: AlbumInfo) : String? {
    return albumInfo.extractor.invoke(albumInfo, node.properties)
}

enum class AlbumInfo(vararg val keys : String, internal val extractor : AlbumInfoExtractor) {

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
    CATALOG("catalog", extractor = EXTRACTOR_DEFAULT),

    /**
     * About CUE CDTEXTFILE command
     *
     * The CDTEXTFILE is used to specify the name of a file that contains the CD-TEXT information.
     *
     * CDTEXTFILE [file name]
     *
     * If the filename contains any spaces, then it must be enclosed in quotation marks.
     */
    CD_TEXT_FILE("cdtextfile", extractor = EXTRACTOR_DEFAULT),

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
    PERFORMER("performer", "albumperformer", extractor = EXTRACTOR_PREFER_FIRST),
    ALBUM_PERFORMER("albumperformer", "performer", extractor = EXTRACTOR_PREFER_FIRST),

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
    SONG_WRITER("songwriter", "albumsongwriter", extractor = EXTRACTOR_PREFER_FIRST),
    ALBUM_SONG_WRITER("albumsongwriter", "songwriter", extractor = EXTRACTOR_PREFER_FIRST),

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
    TITLE("title", "albumtitle", extractor = EXTRACTOR_PREFER_FIRST),
    ALBUM_TITLE("albumtitle", "title", extractor = EXTRACTOR_PREFER_FIRST),

    /**
     * An id for the disc. Typically the freedb disc id.
     */
    DISC_ID("discid", extractor = EXTRACTOR_DEFAULT),

    /**
     * Genre of the album.
     */
    GENRE("genre", extractor = EXTRACTOR_DEFAULT),

    /**
     * Year of the album.
     */
    YEAR("YEAR", extractor = EXTRACTOR_DEFAULT)
//    TRACK_TITLE("TRACKTITLE"),
}