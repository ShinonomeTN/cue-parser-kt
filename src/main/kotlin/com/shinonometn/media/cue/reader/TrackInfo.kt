package com.shinonometn.media.cue.reader

fun CueTrack.getInfo(trackInfo: TrackInfo) : String? {
    var result : String? = null
    for(key in trackInfo.key) {
        result = node.properties[key]
        if(result != null) break
    }
    return result
}

enum class TrackInfo(internal vararg val key : String) {
    PERFORMER("performer"),
    SONG_WRITER("songwriter"),
    TITLE("title", "track_title"),
    ISRC("isrc", "isrc_code"),
    NUMBER("tracknumber"),
    PREFORMER("preformer", "trackpreformer"),
    SONGWRITER("songwriter", "tracksongwriter"),
    YEAR("year"),
    GENRE("genre")
}