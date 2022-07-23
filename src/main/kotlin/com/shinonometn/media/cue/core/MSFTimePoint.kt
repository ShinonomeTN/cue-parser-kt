package com.shinonometn.media.cue.core

import kotlin.math.abs
import kotlin.math.ceil


private val msfPattern = Regex("^([+-])?(\\d+?):(\\d{2}):(\\d{2})$")

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
class MSFTimePoint {

    var minutes: Int = 0
        private set

    var seconds: Int = 0
        private set

    var frames: Int = 0
        private set

    var isPositive : Boolean = true
        private set

    constructor()

    constructor(minutes : Int, seconds : Int, frames : Int, positive : Boolean) {
        this.minutes = minutes
        this.seconds = seconds
        this.frames = frames
        this.isPositive = positive
    }

    private fun setByFrames(f: Int) {
        minutes = f / (75 * 60)
        seconds = (f / 75) % 60
        this.frames = f % 75
    }

    operator fun plus(other: MSFTimePoint): MSFTimePoint {
        return fromFrame(this.totalFrames() + other.totalFrames())
    }

    operator fun minus(other: MSFTimePoint): MSFTimePoint {
        return fromFrame(this.totalFrames() - other.totalFrames())
    }

    operator fun plusAssign(other: MSFTimePoint) {
        val c = this.totalFrames() + other.totalFrames()
        setByFrames(c)
    }

    operator fun minusAssign(other: MSFTimePoint) {
        val c = this.totalFrames() - other.totalFrames()
        setByFrames(c)
    }

    fun toMillis(): Long {
        var millis = 0L
        millis += minutes * 60 * 1000
        millis += seconds * 1000
        millis += ceil((75 * 1000.0) / frames).toInt()
        return millis
    }

    fun toSeconds(): Double {
        return toMillis().toDouble() / 1000
    }

    fun toMinutes(): Double {
        return toMillis().toDouble() / (60 * 1000)
    }

    fun format(): String {
        return String.format("%s%02d:%02d:%02d", if(!isPositive) "-" else "" ,minutes, seconds, frames)
    }

    fun totalFrames() : Int {
        return (minutes * seconds * frames).let { if(!isPositive) it * -1 else it }
    }

    companion object {
        fun fromFrame(frames : Int) : MSFTimePoint {
            return MSFTimePoint().apply { setByFrames(frames) }
        }
    }
}

fun String.toMSFTimePoint(): MSFTimePoint {
    val matching = msfPattern.matchEntire(this) ?: throw NumberFormatException("Invalid format of MSF")
    val (sign, minutes, seconds, frames) = matching.destructured

    return MSFTimePoint(
        minutes = minutes.toInt().takeIf { it >= 0 } ?: throw NumberFormatException("Minutes must be greater than or equal to 0"),
        seconds = seconds.toInt().takeIf { it in 0..59 } ?: throw NumberFormatException("Seconds must be between 0 and 59"),
        frames = frames.toInt().takeIf { it in 0..75 } ?: throw NumberFormatException("Frame must be between 0..75"),
        positive = sign != "-"
    )
}

fun Long.toMSFTimePoint(): MSFTimePoint {
    val a = abs(this)
    return MSFTimePoint(
        minutes = (a / (60 * 1000)).toInt(),
        seconds = (a / 1000 % 60).toInt(),
        frames = ceil((a % 1000) * 75 / 1000.0).toInt(),
        positive = this >= 0
    )
}