package com.shinonometn.media.cue.builder

import com.shinonometn.media.cue.core.MutableCueTreeNode

interface CueAlbumInfoBuilderContext : CuePerformerInfoBuilderContext {
    fun albumperformer(name: String) : MutableCueTreeNode {
        return meta("albumperformer".toUpperCase(), name)
    }

    fun albumsongwriter(name: String) : MutableCueTreeNode {
        return meta("albumsongwriter".toUpperCase(), name)
    }

    fun albumtitle(name: String) : MutableCueTreeNode {
        return meta("albumtitle".toUpperCase(), name)
    }
}