package com.shinonometn.media.cue.reader

import com.shinonometn.media.cue.parser.*

/**
 * Get track list of a media file
 */
fun CueMediaFile.trackList(): List<CueTrack> {
    return node.children.filter { it.type == CueTreeNodeType.TRACK }.map {
        CueTrack(
            it.properties[CUE_PROPERTY_TRACK_NUMBER]?.toInt() ?: error("TRACK node has no number"),
            it.properties[CUE_PROPERTY_TRACK_TYPE] ?: "",
            it
        )
    }
}

class CueTrack(val number: Int, val type: String, val node: CueTreeNode) {

    val indexList: List<CueTrackIndex> by lazy {
        node.properties.entries.filter { it.key.startsWith(CUE_PROPERTY_TRACK_INDEX_PREFIX) }.map {
            val trackNumber = it.key.removePrefix(CUE_PROPERTY_TRACK_INDEX_PREFIX)
            CueTrackIndex(
                trackNumber.toInt(),
                asMSFTimePoint(it.value)
            )
        }
    }

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
    fun getPreGap(): MSFTimePoint? = indexList.find { it.number == 0 }?.time ?:
    // If index 0 not found, try finding PREGAP command
    node.properties["pregap"]?.let {
        asMSFTimePoint(it)
    }

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
    fun getPostGap() : MSFTimePoint? = node.properties["postgap"]?.let {
        asMSFTimePoint(it)
    }

    fun getStart(): CueTrackIndex? = indexList.find { it.number == 1 }

    fun getSubIndex(): List<CueTrackIndex> = indexList.filter { it.number != 0 || it.number != 1 }
}

/**
 * About CUE Index command:
 *
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
class CueTrackIndex(val number: Int, val time: MSFTimePoint)