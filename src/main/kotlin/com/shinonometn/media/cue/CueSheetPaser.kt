package com.shinonometn.media.cue

import java.util.*

class CueSheetPaser(private val cueSheetFile: CueSheetFile) {
    private enum class Status {
        META, TRACK
    }

    private val currentTrackBucket = LinkedList<Pair<CueMetaFieldType, CueSheetLine>>()

    private var parseStatus = Status.META

    private val errorInfo = cueSheetFile.errorInfo
    private val metaInfo = cueSheetFile.metaInfo
    private val trackInfo = cueSheetFile.trackList

    fun parse() {
        for (line in cueSheetFile.lines) {
            doEachLine(line)
        }

        clearTrackBucket()
    }

    private fun doEachLine(line: CueSheetLine) {
        if (line.isComment) return

        val command = CueMetaFieldType.byFieldName(line.command)
        if (command == CueMetaFieldType.UNKNOWN) {
            errorInfo.add(line to "unknown_command: [${line.command}]")
            return
        }

        if (command == CueMetaFieldType.TRACK) {
            parseStatus = Status.TRACK
        }

        when (parseStatus) {
            Status.META -> doOnMetaInfo(command, line)
            Status.TRACK -> doOnTrack(command, line)
        }
    }

    private fun doOnMetaInfo(command: CueMetaFieldType, line: CueSheetLine) {
        if (CueSheetFieldValidator.isMainCommand(command)) {
            if (!CueSheetFieldValidator.validateMainCommandLine(
                    command,
                    line
                )
            ) errorInfo.add(line to "invail_line_format")
            metaInfo.add(command to line)
        } else {
            errorInfo.add(line to "invalid_command_in_main_section: [${command}]")
        }
    }


    private fun doOnTrack(command: CueMetaFieldType, line: CueSheetLine) {
        if (command == CueMetaFieldType.TRACK) {
            clearTrackBucket()

            currentTrackBucket.add(command to line)
            return
        }

        when {
            CueSheetFieldValidator.isTrackCommand(command) -> {
                if (!CueSheetFieldValidator.validateTrackCommandLine(
                        command,
                        line
                    )
                ) errorInfo.add(line to "invail_line_format")
                currentTrackBucket.add(command to line)
            }

            CueSheetFieldValidator.isMainCommand(command) -> {
                if (!CueSheetFieldValidator.validateMainCommandLine(
                        command,
                        line
                    )
                ) errorInfo.add(line to "invail_line_format")

                metaInfo.add(command to line)
                parseStatus = Status.META
            }

            else -> {
                errorInfo.add(line to "invalid_command_in_track_section: [${command.directive}]")
            }
        }
    }

    private fun clearTrackBucket() = with(currentTrackBucket) {
        if (isNotEmpty()) {
            trackInfo.add(CueSheetTrack(first, filter { i ->
                i.second.command != CueMetaFieldType.TRACK.directive
            }))

            clear()
        }
    }
}