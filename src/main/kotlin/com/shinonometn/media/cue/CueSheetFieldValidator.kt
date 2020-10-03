package com.shinonometn.media.cue

object CueSheetFieldValidator {
    /*
     *
     * Frequently used Regex
     *
     * */
    private val REGEX_INDEX_NUMBER = Regex("\\d{1,2}")
    private val REGEX_TRACK_TYPE = Regex("(AUDIO|CDG|(MODE1|MODE2)/(2048|2352)|MODE2/(2324|2336)|CDI/(2336|2352))")
    private val REGEX_DISC_ID = Regex("\\d+")
    private val REGEX_MSF = Regex("(\\d{1,2}:){2}(\\d{1,2})(.\\d{1,2})?")

    /*
    *
    * Cue command validators
    *
    * */
    private val VALIDATOR_IGNORE: (CueSheetLine) -> Boolean = { true }

    private val mainCommandValidators = mapOf(
        CueMetaFieldType.CATALOG to VALIDATOR_IGNORE,
        CueMetaFieldType.CD_TEXT_FILE to VALIDATOR_IGNORE,
        CueMetaFieldType.FILE to VALIDATOR_IGNORE,
        // Track is a special command
        CueMetaFieldType.TRACK to { l ->
            l.sections.size == 3 && l.sections[1].matches(REGEX_INDEX_NUMBER) && l.sections[2].matches(REGEX_TRACK_TYPE)
        },
        // CD Text command
        CueMetaFieldType.TITLE to VALIDATOR_IGNORE,
        CueMetaFieldType.PERFORMER to VALIDATOR_IGNORE,
        CueMetaFieldType.SONG_WRITER to VALIDATOR_IGNORE,
        CueMetaFieldType.ISRC to VALIDATOR_IGNORE,
        CueMetaFieldType.ISRC_CODE to VALIDATOR_IGNORE,
        // Extra CD Text command
        CueMetaFieldType.ALBUM_TITLE to VALIDATOR_IGNORE,
        CueMetaFieldType.ALBUM_PERFORMER to VALIDATOR_IGNORE,
        CueMetaFieldType.ALBUM_SONG_WRITER to VALIDATOR_IGNORE,
        CueMetaFieldType.ALBUM_COMMENT to VALIDATOR_IGNORE,
        CueMetaFieldType.ALBUM_GENRE to VALIDATOR_IGNORE,
        CueMetaFieldType.ALBUM_YEAR to VALIDATOR_IGNORE,
        CueMetaFieldType.DISC_ID to { l -> l.sections.size == 2 && l.sections[1].matches(REGEX_DISC_ID) },
        // Remark is a special command
        CueMetaFieldType.REMARK to { l -> l.sections.size >= 2 }
    )
    private val mainCommandGroup = mainCommandValidators.keys

    fun isMainCommand(type: CueMetaFieldType): Boolean {
        return mainCommandGroup.contains(type)
    }

    private val trackCommandValidators = mapOf(
        CueMetaFieldType.FLAGS to VALIDATOR_IGNORE,
        CueMetaFieldType.INDEX to { l ->
            l.sections.size == 3 && l.sections[1].matches(REGEX_INDEX_NUMBER) && l.sections[2].matches(REGEX_MSF)
        },
        CueMetaFieldType.TRACK_NUMBER to VALIDATOR_IGNORE,
        CueMetaFieldType.POSTGAP to { l -> l.sections.size == 2 && l.sections[1].matches(REGEX_MSF) },
        CueMetaFieldType.PREGAP to { l -> l.sections.size == 2 && l.sections[1].matches(REGEX_MSF) },
        // CD Text command
        CueMetaFieldType.TITLE to VALIDATOR_IGNORE,
        CueMetaFieldType.PERFORMER to VALIDATOR_IGNORE,
        CueMetaFieldType.SONG_WRITER to VALIDATOR_IGNORE,
        CueMetaFieldType.ISRC to VALIDATOR_IGNORE,
        // Extra CD Text command
        CueMetaFieldType.ISRC_CODE to VALIDATOR_IGNORE,
        CueMetaFieldType.TRACK_TITLE to VALIDATOR_IGNORE,
        CueMetaFieldType.TRACK_PERFORMER to VALIDATOR_IGNORE,
        CueMetaFieldType.TRACK_PERFORMER to VALIDATOR_IGNORE,
        CueMetaFieldType.TRACK_SONG_WRITER to VALIDATOR_IGNORE,
        // Remark is a special command
        CueMetaFieldType.REMARK to { l -> l.sections.size >= 2 }
    )
    private val trackCommandGroup = trackCommandValidators.keys

    fun isTrackCommand(type: CueMetaFieldType): Boolean {
        return trackCommandGroup.contains(type)
    }

    fun validateTrackCommandLine(cueMetaFieldType: CueMetaFieldType, cueLine: CueSheetLine): Boolean {
        return trackCommandValidators.getOrElse(cueMetaFieldType) {
            { true }
        }.invoke(cueLine)
    }

    fun validateMainCommandLine(metaType: CueMetaFieldType, cueLine: CueSheetLine): Boolean {
        return mainCommandValidators.getOrElse(metaType) { { true } }.invoke(cueLine)
    }
}