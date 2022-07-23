package com.shinonometn.media.cue.reader

fun CueTrack.trackInfo(trackInfo: TrackInfo): String? {
    return with(trackInfo) { extractor(node) }
}

open class TrackInfo internal constructor(
    vararg key: String, extractor: Extractor = EXTRACTOR_DEFAULT
) : MetaPropertyReader(*key, extractor = extractor) {

    object Title : TrackInfo("title", "track_title")
//    object TrackTitle : TrackInfo("track_title")

    object Performer : TrackInfo("performer", "trackpreformer", extractor = EXTRACTOR_DEFAULT)
//    object TrackPerformer : TrackInfo("trackpreformer")

    object SongWriter : TrackInfo("songwriter", "tracksongwriter", extractor = EXTRACTOR_DEFAULT)
//    object TrackSongWriter : TrackInfo("tracksongwriter")

    object ISRC : TrackInfo("isrc", "isrc_code", extractor = EXTRACTOR_DEFAULT)

    object Number : TrackInfo("tracknumber")

    object Year : TrackInfo("year")

    object Genre : TrackInfo("genre")
}