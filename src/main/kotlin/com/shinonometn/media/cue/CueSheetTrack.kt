package com.shinonometn.media.cue

/*
*
* Track entity
*
* */
data class CueSheetTrack(
    private val line: Pair<CueMetaFieldType, CueSheetLine>,
    val lines: Collection<Pair<CueMetaFieldType, CueSheetLine>>
) {
    val self = line.second
    val type = line.first
}