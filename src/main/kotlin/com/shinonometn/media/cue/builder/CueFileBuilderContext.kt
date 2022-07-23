package com.shinonometn.media.cue.builder

interface CueFileBuilderContext : CueBaseBuilderContext {
    /**
     * Description: Starts a new track.
     *
     * Section(s): track
     *
     * Format: `TRACK <track_number> <track_mode>`
     * - track_number: a positive integer in the range 1-99.
     * - track_mode: one of: "AUDIO", "CDG", "MODE1/2048", "MODE1/2352", "MODE2/2336", "MODE2/2352", "CDI/2336", or "CDI/2352".
     *
     * **About CUE TRACK command :**
     *
     * The TRACK command is used to specify a track.
     *
     * The track number should be a number ranging from 1 to 99.
     * It is unclear of the index number should always be represented by 2 digits.
     *
     * The first track number can be greater than one, but all track numbers after the first must be sequential.
     * The CUE sheet should at least contain 1 TRACK command.
     *
     * Note: An unspecified gap between tracks is allowed.
     * */
    @CueBuilderDsl
    fun track(number : Int, mode : String, builder : CueTrackBuilderContext.() -> Unit = {}) : CueTrackBuilderContext
}