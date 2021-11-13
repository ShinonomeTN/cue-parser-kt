package com.shinonometn.media.cue.reader

internal typealias Extractor = MetaPropertyReader.(Map<String, String>) -> String?

open class MetaPropertyReader constructor(vararg val keys: String, val extractor: Extractor) {
    companion object {
        val DefaultExtractor = EXTRACTOR_DEFAULT
        val PreferFirstExtractor = EXTRACTOR_PREFER_FIRST
    }
}

internal val EXTRACTOR_DEFAULT : Extractor = { it[keys[0]] }

internal val EXTRACTOR_PREFER_FIRST : Extractor = {
    var content : String? = null
    for(key in keys) {
        content = it[key]
        if(content != null) break
    }
    content
}