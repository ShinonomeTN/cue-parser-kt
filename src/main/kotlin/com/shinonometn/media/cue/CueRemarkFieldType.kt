package com.shinonometn.media.cue

/**
 *
 * According to https://github.com/libyal/libodraw/blob/master/documentation/CUE%20sheet%20format.asciidoc#4-remarks-commands
 *
 * */
enum class CueRemarkFieldType(val field: String) {
    DATE("DATE"),
    REPLAY_GAIN_ALBUM_GAIN("REPLAYGAIN_ALBUM_GAIN"),
    REPLAY_GAIN_ALBUM_PEAK("REPLAYGAIN_ALBUM_PEAK"),
    REPLAY_GAIN_TRACK_GAIN("REPLAYGAIN_TRACK_GAIN"),
    REPLAY_GAIN_TRACK_PEAK("REPLAYGAIN_TRACK_PEAK"),
    LEAD_OUT("LEAD-OUT"),
    MSF("MSF"),
    ORIGINAL("ORIGINAL"),
    RUN_OUT("RUN-OUT"),
    SESSION("SESSION"),
    GENRE("GENRE"),
    COMMENT("COMMENT"),
    DISC_ID("DISCID"),
    UNKNOWN("UNKNOWN");

    companion object {
        private val reverseMap = (values().asList() indexToMap { i -> i.field })

        fun byFieldName(fieldName: String) = reverseMap[fieldName] ?: UNKNOWN
    }
}