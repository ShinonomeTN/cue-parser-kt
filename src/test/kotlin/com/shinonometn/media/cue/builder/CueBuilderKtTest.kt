package com.shinonometn.media.cue.builder

import com.shinonometn.media.cue.core.toMSFTimePoint
import org.junit.Test

class CueBuilderKtTest {
    @Test
    fun `Test build cue sheet`() {
        println(CueSheet {
            rem("COMMENT", "This is a comment")
            comment("This is also a comment")
            file("Hallo Event.wav", "WAV") {
                track(1, "AUDIO") {
                    index(1, "00:00:00".toMSFTimePoint())
                    meta("TITLE", "Hallo Event")
                }
            }
        }.build())
    }
}