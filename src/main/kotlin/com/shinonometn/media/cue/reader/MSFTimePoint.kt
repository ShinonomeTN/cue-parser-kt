package com.shinonometn.media.cue.reader

/**
 *
 * The MSF consists of minutes:seconds:frames (mm:ss:ff), the
 * MSF is either relative to the last FILE command or relative to
 * the start of the optical media. There are 75 frames per second,
 * 60 seconds per minute.
 *
 * Note that the MSF is defined without the 2 seconds MFS offset,
 * therefore MSF 00:00:00 equals LBA 0. This is different from the
 * MSF for the CD itself where MSF 00:02:00 equals LBA 0.
 *
 */
data class MSFTimePoint(var minutes: Int, var seconds: Int, var frames: Int)

private val regex = Regex("^(\\d{2}):(\\d{2}):(\\d{2})$")

fun asMSFTimePoint(string: String): MSFTimePoint {
    val matching = regex.matchEntire(string) ?: throw NumberFormatException("Invalid format of MSF")

    return MSFTimePoint(
        minutes = matching.groupValues[0].toInt(),
        seconds = matching.groupValues[1].toInt(),
        frames = matching.groupValues[2].toInt()
    )
}

fun asMSFTimePoint(millis: Long): MSFTimePoint {
    return MSFTimePoint(
        minutes = (millis / (60 * 1000)).toInt(),
        seconds = (millis / 1000 % 60).toInt(),
        frames = ((millis % 1000) * 75 / 1000).toInt()
    )
}

fun MSFTimePoint.toMillis(): Long {
    var millis = 0L
    millis += minutes * 60 * 1000
    millis += seconds * 1000
    millis += (75 * 1000) / frames
    return millis
}

fun MSFTimePoint.toSeconds(): Double {
    return toMillis().toDouble() / 1000
}

fun MSFTimePoint.toMinutes(): Double {
    return toMillis().toDouble() / (60 * 1000)
}

fun MSFTimePoint.format(): String {
    return String.format("%02d:%02d:%02d", minutes, seconds, frames)
}