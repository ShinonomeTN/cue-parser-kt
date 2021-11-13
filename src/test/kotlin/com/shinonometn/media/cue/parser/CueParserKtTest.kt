package com.shinonometn.media.cue.parser

import com.shinonometn.media.cue.reader.*
import org.junit.Assert.*
import org.junit.Test
import java.util.logging.Logger

class CueParserKtTest {

    private val logger = Logger.getLogger("CueParserTest")

    private fun readCue() = CueParser(CueParserKtTest::class.java.getResourceAsStream("/test-reading-cue-file.cue")!!)

    @Test
    fun `Test reading cue file`() {
        val cueTree = readCue()
        logger.info(cueTree.toString())
    }

    @Test
    fun `Test show album info`() {
        val reader = readCue().reader()

        logger.info("""
            Cue file information:
            ------------------------------------------------
            Title    : ${reader.albumInfo(AlbumInfo.Title)}
            Performer: ${reader.albumInfo(AlbumInfo.Performer)}
            Genre    : ${reader.albumInfo(AlbumInfo.Genre)}
            Date     : ${reader.albumInfo(AlbumInfo.Date)}
            ------------------------------------------------
            Raw data:
            ------------------------------------------------
            ${reader.rootNode.properties}
        """.trimIndent())
    }

    @Test
    fun `Test show track info`() {
        val reader = readCue().reader()

        val mediaFiles = reader.mediaFileList()

        logger.info("${reader.albumInfo(AlbumInfo.Title)} has ${mediaFiles.size} file.")
        assertTrue("File list should not be empty.",mediaFiles.isNotEmpty())

        val file = mediaFiles.first()
        logger.info(""" 
            Media Files:
            ---------------------------------------
            Filename :${file.filename}
            Type     :${file.type}
            ---------------------------------------
        """.trimIndent())

        val trackList = file.trackList()

        logger.info("Total ${trackList.size} track(s).")
        assertTrue("Track list should not be empty.", trackList.isNotEmpty())

        trackList.forEach {
            logger.info("""
                Track ${it.number} Info:
                ---------------------------------------
                Type     :${it.type}
                Title    : ${it.trackInfo(TrackInfo.Title)}
                Performer: ${it.trackInfo(TrackInfo.Performer)}
                ---------------------------------------
                Raw data :
                ${it.node.properties}
                ---------------------------------------
            """.trimIndent())
        }

    }
}