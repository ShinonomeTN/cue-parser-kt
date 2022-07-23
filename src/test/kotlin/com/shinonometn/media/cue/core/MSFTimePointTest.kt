package com.shinonometn.media.cue.core

import org.junit.Assert.*
import org.junit.Test

class MSFTimePointTest {

    @Test
    fun `Parse time point 00_00_00`() {
        val timePoint = "00:00:00".toMSFTimePoint()
        assertEquals(0, timePoint.minutes)
        assertEquals(0, timePoint.seconds)
        assertEquals(0, timePoint.frames)
        assertTrue(timePoint.isPositive)
    }

    @Test
    fun `Parse time point +00_00_00`() {
        val timePoint = "+00:00:00".toMSFTimePoint()
        assertTrue(timePoint.isPositive)
    }

    @Test
    fun `Parse time point -00_00_00`() {
        val timePoint = "-00:00:00".toMSFTimePoint()
        assertFalse(timePoint.isPositive)
    }

    @Test
    fun `Parse time point with frames`() {
        val frames = 75 * 1
        val timePoint = MSFTimePoint.fromFrame(frames)
        assertEquals(0, timePoint.minutes)
        assertEquals(1, timePoint.seconds)
        assertEquals(0, timePoint.frames)
    }

    @Test
    fun `Parse time point with millis`() {
        val millis = 60 * 1000L
        val timePoint = millis.toMSFTimePoint()
        assertEquals(1, timePoint.minutes)
        assertEquals(0, timePoint.seconds)
    }
}